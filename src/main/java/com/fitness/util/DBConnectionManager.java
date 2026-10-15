package com.fitness.util;

import com.fitness.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * High-performance JDBC Connection Manager utilizing HikariCP connection pool.
 * Loads settings from environment variables with fallback to db.properties.
 */
public class DBConnectionManager {

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
        long connTimeout = Long.parseLong(props.getProperty("db.pool.connectionTimeoutMs", "20000"));

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
            this.dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully for URL: {}", url);
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool: {}", e.getMessage());
            // Do not crash classloader immediately; queries will throw descriptive DatabaseException
        }
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
