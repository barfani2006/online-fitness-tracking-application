package com.fitness.util;

import com.fitness.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * High-performance JDBC Connection Manager utilizing HikariCP connection pool.
 * Loads settings from environment variables with fallback to db.properties.
 * Includes an automated embedded in-memory database fallback to ensure seamless local operation
 * even if external MySQL is unreachable or credentials have not been configured yet.
 */
public class DBConnectionManager implements ConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(DBConnectionManager.class);
    private static volatile DBConnectionManager instance;
    private HikariDataSource dataSource;

    private DBConnectionManager() {
        initDataSource();
    }

    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) {
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    private void initDataSource() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            logger.warn("Could not load db.properties, proceeding with defaults and environment variables.", e);
        }

        // Environment variables take precedence over db.properties
        String driver = getEnvOrProp("DB_DRIVER", props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        String url = getEnvOrProp("DB_URL", props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/fitness_tracker_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"));
        String username = getEnvOrProp("DB_USER", props.getProperty("db.username", "root"));
        String password = getEnvOrProp("DB_PASSWORD", props.getProperty("db.password", "root123"));

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        int maxPoolSize = Integer.parseInt(props.getProperty("db.pool.maxSize", "10"));
        int minIdle = Integer.parseInt(props.getProperty("db.pool.minIdle", "2"));
        long idleTimeout = Long.parseLong(props.getProperty("db.pool.idleTimeoutMs", "300000"));
        long connTimeout = Long.parseLong(props.getProperty("db.pool.connectionTimeoutMs", "5000")); // 5s timeout

        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connTimeout);
        config.setPoolName("FitnessApp-HikariPool");

        // Useful MySQL driver optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        try {
            HikariDataSource ds = new HikariDataSource(config);
            // Verify connection works immediately
            try (Connection testConn = ds.getConnection()) {
                logger.info("HikariCP connected and verified successfully for MySQL URL: {}", url);
            }
            this.dataSource = ds;
        } catch (Exception e) {
            logger.warn("Could not connect to external MySQL database ({}). Initializing automatic embedded H2 database fallback...", e.getMessage());
            initH2Fallback();
        }
    }

    private void initH2Fallback() {
        try {
            HikariConfig h2Config = new HikariConfig();
            h2Config.setDriverClassName("org.h2.Driver");
            h2Config.setJdbcUrl("jdbc:h2:mem:fitness_tracker_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1");
            h2Config.setUsername("sa");
            h2Config.setPassword("");
            h2Config.setMaximumPoolSize(10);
            h2Config.setPoolName("FitnessApp-H2FallbackPool");

            this.dataSource = new HikariDataSource(h2Config);
            logger.info("Embedded H2 database pool started. Initializing schema and seed data...");

            try (Connection conn = this.dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                executeH2Init(stmt);
            }
            logger.info("Embedded H2 database initialized and seeded successfully! Application is ready.");
        } catch (Exception ex) {
            logger.error("Failed to initialize embedded H2 database fallback", ex);
        }
    }

    private void executeH2Init(Statement stmt) throws SQLException {
        // 1. Users Table
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(150) NOT NULL UNIQUE, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL DEFAULT 'USER', " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "age INT DEFAULT NULL, " +
                "gender VARCHAR(20) DEFAULT 'PREFER_NOT_TO_SAY', " +
                "weight_kg DECIMAL(5,2) DEFAULT NULL, " +
                "height_cm DECIMAL(5,2) DEFAULT NULL, " +
                "fitness_level VARCHAR(20) DEFAULT 'BEGINNER', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 2. Workout Categories Table
        stmt.execute("CREATE TABLE IF NOT EXISTS workout_categories (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL UNIQUE, " +
                "description VARCHAR(255) DEFAULT NULL, " +
                "met_value DECIMAL(4,2) NOT NULL DEFAULT 4.00, " +
                "icon_name VARCHAR(50) DEFAULT 'dumbbell', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 3. Workouts Table
        stmt.execute("CREATE TABLE IF NOT EXISTS workouts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "category_id INT DEFAULT NULL, " +
                "activity_name VARCHAR(100) NOT NULL, " +
                "workout_date DATE NOT NULL, " +
                "duration_minutes INT NOT NULL, " +
                "intensity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', " +
                "calories_burned INT NOT NULL, " +
                "notes TEXT DEFAULT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 4. Fitness Goals Table
        stmt.execute("CREATE TABLE IF NOT EXISTS fitness_goals (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "title VARCHAR(150) NOT NULL, " +
                "goal_type VARCHAR(30) NOT NULL, " +
                "target_value DECIMAL(8,2) NOT NULL, " +
                "current_value DECIMAL(8,2) NOT NULL DEFAULT 0.00, " +
                "unit VARCHAR(30) NOT NULL, " +
                "start_date DATE NOT NULL, " +
                "target_date DATE NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 5. Fitness Challenges Table
        stmt.execute("CREATE TABLE IF NOT EXISTS fitness_challenges (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(150) NOT NULL, " +
                "description TEXT NOT NULL, " +
                "target_metric VARCHAR(30) NOT NULL, " +
                "target_goal INT NOT NULL, " +
                "duration_days INT NOT NULL, " +
                "badge_name VARCHAR(100) NOT NULL, " +
                "start_date DATE NOT NULL, " +
                "end_date DATE NOT NULL, " +
                "created_by INT DEFAULT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 6. User Challenges Participation Table
        stmt.execute("CREATE TABLE IF NOT EXISTS user_challenges (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "challenge_id INT NOT NULL, " +
                "joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "current_progress INT NOT NULL DEFAULT 0, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS', " +
                "completed_at TIMESTAMP NULL DEFAULT NULL)");

        // 7. Fitness Content Table
        stmt.execute("CREATE TABLE IF NOT EXISTS fitness_content (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "author_id INT DEFAULT NULL, " +
                "title VARCHAR(200) NOT NULL, " +
                "category VARCHAR(30) NOT NULL, " +
                "content TEXT NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'PENDING', " +
                "rejection_reason TEXT DEFAULT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 8. Application Settings Table
        stmt.execute("CREATE TABLE IF NOT EXISTS app_settings (" +
                "setting_key VARCHAR(50) PRIMARY KEY, " +
                "setting_value VARCHAR(255) NOT NULL, " +
                "description VARCHAR(255) DEFAULT NULL, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // 9. Audit Logs Table
        stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT DEFAULT NULL, " +
                "action VARCHAR(50) NOT NULL, " +
                "entity_type VARCHAR(50) DEFAULT NULL, " +
                "entity_id INT DEFAULT NULL, " +
                "details TEXT DEFAULT NULL, " +
                "ip_address VARCHAR(45) DEFAULT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // Seed Core Categories
        stmt.execute("MERGE INTO workout_categories KEY(id) VALUES " +
                "(1, 'Running', 'Endurance running and sprint intervals', 8.50, 'running', CURRENT_TIMESTAMP), " +
                "(2, 'Cycling', 'Outdoor road cycling and spin training', 7.00, 'bicycle', CURRENT_TIMESTAMP), " +
                "(3, 'Strength Training', 'Resistance and weightlifting movements', 5.50, 'dumbbell', CURRENT_TIMESTAMP), " +
                "(4, 'Swimming', 'Full-body cardiovascular swimming laps', 7.50, 'swimming', CURRENT_TIMESTAMP), " +
                "(5, 'Yoga & Mobility', 'Vinyasa flow, stretching, and mobility', 3.00, 'spa', CURRENT_TIMESTAMP), " +
                "(6, 'HIIT', 'High-Intensity Interval Training bursts', 9.50, 'bolt', CURRENT_TIMESTAMP), " +
                "(7, 'Walking', 'Brisk aerobic walking and hiking', 3.80, 'walking', CURRENT_TIMESTAMP), " +
                "(8, 'Pilates', 'Core stability and postural alignment', 4.00, 'heart', CURRENT_TIMESTAMP)");

        // Seed Users (Support both pulsefit.com and fitness.com emails with BCrypt hashes)
        // Admin password hash corresponds to 'Admin@123'
        // User password hash corresponds to 'User@123'
        stmt.execute("MERGE INTO users KEY(email) VALUES " +
                "(1, 'System Administrator', 'admin@pulsefit.com', '$2a$12$c9lhq/rt1FvLB.nOyyatOu4hOQbRHz9sxp5OFnz/Keyq7x1RxwM/.', 'ADMIN', 'ACTIVE', 32, 'OTHER', 72.0, 175.0, 'ADVANCED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), " +
                "(2, 'System Administrator', 'admin@fitness.com', '$2a$12$c9lhq/rt1FvLB.nOyyatOu4hOQbRHz9sxp5OFnz/Keyq7x1RxwM/.', 'ADMIN', 'ACTIVE', 32, 'OTHER', 72.0, 175.0, 'ADVANCED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), " +
                "(3, 'Alex Miller', 'alex.miller@gmail.com', '$2a$12$SF3VcDj8F2ZZfO3A93lq8OqpZsNlFj2Le6Sh/pwimsNmtDkisBzky', 'USER', 'ACTIVE', 24, 'MALE', 78.5, 180.0, 'INTERMEDIATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), " +
                "(4, 'John Doe', 'john@fitness.com', '$2a$12$SF3VcDj8F2ZZfO3A93lq8OqpZsNlFj2Le6Sh/pwimsNmtDkisBzky', 'USER', 'ACTIVE', 24, 'MALE', 78.5, 180.0, 'INTERMEDIATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), " +
                "(5, 'Sarah Jenkins', 'sarah@fitness.com', '$2a$12$SF3VcDj8F2ZZfO3A93lq8OqpZsNlFj2Le6Sh/pwimsNmtDkisBzky', 'USER', 'ACTIVE', 28, 'FEMALE', 62.0, 168.0, 'BEGINNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        // Seed Workouts for User 3 (alex.miller) and User 4 (john)
        stmt.execute("INSERT INTO workouts (user_id, category_id, activity_name, workout_date, duration_minutes, intensity, calories_burned, notes) VALUES " +
                "(3, 1, 'Morning 5K Park Run', CURRENT_DATE - 6, 32, 'HIGH', 340, 'Paced at 6:24/km.'), " +
                "(3, 3, 'Chest & Triceps Hypertrophy', CURRENT_DATE - 5, 50, 'HIGH', 380, 'Bench press 4x8 @ 75kg.'), " +
                "(3, 5, 'Recovery Vinyasa Flow', CURRENT_DATE - 4, 30, 'LOW', 110, 'Focus on hip mobility.'), " +
                "(3, 6, 'Full-Body HIIT Circuit', CURRENT_DATE - 3, 25, 'EXTREME', 320, 'Tabata intervals.'), " +
                "(3, 2, 'Evening Lakeshore Ride', CURRENT_DATE - 2, 45, 'MEDIUM', 390, 'Average speed 22 km/h.'), " +
                "(3, 3, 'Back & Biceps Heavy Day', CURRENT_DATE - 1, 55, 'HIGH', 410, 'Deadlifts 4x6 @ 110kg.'), " +
                "(3, 1, 'Interval Sprint Track Session', CURRENT_DATE, 35, 'EXTREME', 420, '400m repeats.'), " +
                "(4, 1, 'Morning 5K Park Run', CURRENT_DATE - 6, 32, 'HIGH', 340, 'Paced at 6:24/km.'), " +
                "(4, 3, 'Chest & Triceps Hypertrophy', CURRENT_DATE - 5, 50, 'HIGH', 380, 'Bench press 4x8 @ 75kg.'), " +
                "(4, 5, 'Recovery Vinyasa Flow', CURRENT_DATE - 4, 30, 'LOW', 110, 'Focus on hip mobility.'), " +
                "(4, 6, 'Full-Body HIIT Circuit', CURRENT_DATE - 3, 25, 'EXTREME', 320, 'Tabata intervals.'), " +
                "(4, 2, 'Evening Lakeshore Ride', CURRENT_DATE - 2, 45, 'MEDIUM', 390, 'Average speed 22 km/h.'), " +
                "(4, 3, 'Back & Biceps Heavy Day', CURRENT_DATE - 1, 55, 'HIGH', 410, 'Deadlifts 4x6 @ 110kg.'), " +
                "(4, 1, 'Interval Sprint Track Session', CURRENT_DATE, 35, 'EXTREME', 420, '400m repeats.')");

        // Seed Goals
        stmt.execute("INSERT INTO fitness_goals (user_id, title, goal_type, target_value, current_value, unit, start_date, target_date, status) VALUES " +
                "(3, 'Monthly Workout Frequency', 'WORKOUT_COUNT', 20.00, 7.00, 'workouts', CURRENT_DATE - 10, CURRENT_DATE + 20, 'ACTIVE'), " +
                "(3, 'Burn 10,000 Calories', 'CALORIES_BURNED', 10000.00, 2370.00, 'kcal', CURRENT_DATE - 10, CURRENT_DATE + 20, 'ACTIVE'), " +
                "(3, 'Reach 75kg Target Bodyweight', 'WEIGHT_TARGET', 75.00, 78.50, 'kg', CURRENT_DATE - 30, CURRENT_DATE + 60, 'ACTIVE'), " +
                "(4, 'Monthly Workout Frequency', 'WORKOUT_COUNT', 20.00, 7.00, 'workouts', CURRENT_DATE - 10, CURRENT_DATE + 20, 'ACTIVE')");

        // Seed Challenges
        stmt.execute("MERGE INTO fitness_challenges KEY(id) VALUES " +
                "(1, '30-Day Consistency Streak', 'Log at least 20 workouts over 30 days to build habit.', 'WORKOUT_COUNT', 20, 30, 'Iron Will Medal', CURRENT_DATE - 10, CURRENT_DATE + 20, 1, CURRENT_TIMESTAMP), " +
                "(2, '1,000 Minutes of Movement', 'Accumulate 1,000 total active exercise minutes.', 'TOTAL_MINUTES', 1000, 30, 'Endurance Titan', CURRENT_DATE - 15, CURRENT_DATE + 15, 1, CURRENT_TIMESTAMP), " +
                "(3, '5,000 Calorie Burn Sprint', 'Torch 5,000 active calories through high-energy workouts.', 'CALORIES', 5000, 14, 'Calorie Crusher', CURRENT_DATE, CURRENT_DATE + 14, 1, CURRENT_TIMESTAMP)");

        // Seed Enrollments
        stmt.execute("INSERT INTO user_challenges (user_id, challenge_id, joined_at, current_progress, status) VALUES " +
                "(3, 1, CURRENT_TIMESTAMP - 8, 7, 'IN_PROGRESS'), " +
                "(3, 2, CURRENT_TIMESTAMP - 12, 272, 'IN_PROGRESS'), " +
                "(4, 1, CURRENT_TIMESTAMP - 8, 7, 'IN_PROGRESS')");

        // Seed Content
        stmt.execute("INSERT INTO fitness_content (author_id, title, category, content, status) VALUES " +
                "(1, 'Mastering Progressive Overload', 'STRENGTH', 'Progressive overload involves gradually increasing weight, frequency, or repetitions in your strength routine.', 'APPROVED'), " +
                "(1, 'The Science of Aerobic Zone 2 Cardio', 'CARDIO', 'Zone 2 training optimizes mitochondrial density, fat oxidation, and cardiovascular stamina without chronic fatigue.', 'APPROVED'), " +
                "(1, 'Hydration and Electrolyte Replenishment', 'NUTRITION', 'Consume 500ml water 2 hours before high-intensity workouts and maintain balanced sodium-potassium intake.', 'APPROVED'), " +
                "(3, 'My 30-Day Running Journey', 'GENERAL_FITNESS', 'How I transitioned from couch to completing consistent 5K morning runs without joint pain.', 'PENDING')");

        // Seed System Settings
        stmt.execute("MERGE INTO app_settings KEY(setting_key) VALUES " +
                "('site_title', 'PulseFit Tracker', 'Application brand name', CURRENT_TIMESTAMP), " +
                "('maintenance_mode', 'false', 'Platform maintenance flag', CURRENT_TIMESTAMP), " +
                "('max_workout_minutes', '720', 'Maximum allowed single workout duration', CURRENT_TIMESTAMP), " +
                "('allow_registration', 'true', 'Enable or disable new user signups', CURRENT_TIMESTAMP), " +
                "('default_pagination_size', '10', 'Number of rows per page in lists', CURRENT_TIMESTAMP)");
    }

    private String getEnvOrProp(String envKey, String fallback) {
        String val = System.getenv(envKey);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        val = System.getProperty(envKey);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        return fallback;
    }

    /**
     * Obtains a connection from the connection pool.
     *
     * @return active java.sql.Connection
     * @throws DatabaseException if pool is unavailable or database cannot be reached
     */
    public Connection getConnection() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (this) {
                if (dataSource == null || dataSource.isClosed()) {
                    initDataSource();
                }
            }
        }
        if (dataSource == null) {
            throw new DatabaseException("Database connection pool is unavailable. Please verify MySQL configuration.");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to acquire database connection: " + e.getMessage(), e);
        }
    }

    /**
     * Safely rolls back an in-progress JDBC transaction.
     */
    public static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("Failed to rollback transaction", e);
            }
        }
    }

    /**
     * Closes an auto-closeable resource quietly without throwing checked exceptions.
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Gracefully shuts down connection pool on servlet context destruction.
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool closed gracefully.");
        }
    }
}
