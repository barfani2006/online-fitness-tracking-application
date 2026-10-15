package com.fitness.dao.impl;

import com.fitness.dao.WorkoutDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.Workout;
import com.fitness.model.WorkoutCategory;
import com.fitness.model.WorkoutIntensity;
import com.fitness.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC implementation of WorkoutDAO with prepared statements, safe transactions, and aggregations.
 */
public class WorkoutDAOImpl implements WorkoutDAO {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutDAOImpl.class);
    private final DBConnectionManager dbManager;

    public WorkoutDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public WorkoutDAOImpl(DBConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<Workout> findById(Integer id) {
        String sql = "SELECT w.*, c.name AS category_name FROM workouts w " +
                "LEFT JOIN workout_categories c ON w.category_id = c.id WHERE w.id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWorkout(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding workout by id: {}", id, e);
            throw new DatabaseException("Failed to find workout: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Workout> findAll() {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT w.*, c.name AS category_name FROM workouts w " +
                "LEFT JOIN workout_categories c ON w.category_id = c.id ORDER BY w.workout_date DESC, w.id DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToWorkout(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all workouts", e);
            throw new DatabaseException("Failed to fetch workouts: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Workout> findByUserId(int userId) {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT w.*, c.name AS category_name FROM workouts w " +
                "LEFT JOIN workout_categories c ON w.category_id = c.id WHERE w.user_id = ? " +
                "ORDER BY w.workout_date DESC, w.id DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWorkout(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding workouts for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch user workouts: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Workout> findRecentByUserId(int userId, int limit) {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT w.*, c.name AS category_name FROM workouts w " +
                "LEFT JOIN workout_categories c ON w.category_id = c.id WHERE w.user_id = ? " +
                "ORDER BY w.workout_date DESC, w.id DESC LIMIT ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWorkout(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching recent workouts for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch recent workouts: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Workout save(Workout w) {
        String sql = "INSERT INTO workouts (user_id, category_id, activity_name, workout_date, duration_minutes, intensity, calories_burned, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, w.getUserId());
            if (w.getCategoryId() != null) stmt.setInt(2, w.getCategoryId());
            else stmt.setNull(2, Types.INTEGER);

            stmt.setString(3, w.getActivityName().trim());
            stmt.setDate(4, Date.valueOf(w.getWorkoutDate() != null ? w.getWorkoutDate() : LocalDate.now()));
            stmt.setInt(5, w.getDurationMinutes());
            stmt.setString(6, w.getIntensity() != null ? w.getIntensity().name() : WorkoutIntensity.MEDIUM.name());
            stmt.setInt(7, w.getCaloriesBurned());
            stmt.setString(8, w.getNotes());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Saving workout failed, no rows affected.");
            }
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    w.setId(gk.getInt(1));
                }
            }
            return w;
        } catch (SQLException e) {
            logger.error("Error inserting workout", e);
            throw new DatabaseException("Failed to record workout: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Workout w) {
        String sql = "UPDATE workouts SET category_id = ?, activity_name = ?, workout_date = ?, duration_minutes = ?, " +
                "intensity = ?, calories_burned = ?, notes = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (w.getCategoryId() != null) stmt.setInt(1, w.getCategoryId());
            else stmt.setNull(1, Types.INTEGER);

            stmt.setString(2, w.getActivityName().trim());
            stmt.setDate(3, Date.valueOf(w.getWorkoutDate()));
            stmt.setInt(4, w.getDurationMinutes());
            stmt.setString(5, w.getIntensity() != null ? w.getIntensity().name() : WorkoutIntensity.MEDIUM.name());
            stmt.setInt(6, w.getCaloriesBurned());
            stmt.setString(7, w.getNotes());
            stmt.setInt(8, w.getId());
            stmt.setInt(9, w.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating workout: {}", w.getId(), e);
            throw new DatabaseException("Failed to update workout: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM workouts WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting workout: {}", id, e);
            throw new DatabaseException("Failed to delete workout: " + e.getMessage(), e);
        }
    }

    public boolean deleteByIdAndUserId(int workoutId, int userId) {
        String sql = "DELETE FROM workouts WHERE id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workoutId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting workout: {} for user: {}", workoutId, userId, e);
            throw new DatabaseException("Failed to delete workout: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Workout> searchWorkouts(int userId, String keyword, Integer categoryId,
                                        LocalDate startDate, LocalDate endDate,
                                        String sortBy, String sortOrder, int offset, int limit) {
        List<Workout> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT w.*, c.name AS category_name FROM workouts w " +
                "LEFT JOIN workout_categories c ON w.category_id = c.id WHERE w.user_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(w.activity_name) LIKE ? OR LOWER(w.notes) LIKE ?) ");
            String wild = "%" + keyword.trim().toLowerCase() + "%";
            params.add(wild);
            params.add(wild);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND w.category_id = ? ");
            params.add(categoryId);
        }
        if (startDate != null) {
            sql.append("AND w.workout_date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND w.workout_date <= ? ");
            params.add(Date.valueOf(endDate));
        }

        // Whitelist sorting column to prevent SQL injection
        String validSortCol = "w.workout_date";
        if ("duration".equalsIgnoreCase(sortBy)) validSortCol = "w.duration_minutes";
        else if ("calories".equalsIgnoreCase(sortBy)) validSortCol = "w.calories_burned";
        else if ("activity".equalsIgnoreCase(sortBy)) validSortCol = "w.activity_name";

        String validOrder = "ASC".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";
        sql.append("ORDER BY ").append(validSortCol).append(" ").append(validOrder).append(", w.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWorkout(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching workouts", e);
            throw new DatabaseException("Failed to search workouts: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public int countWorkouts(int userId, String keyword, Integer categoryId, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM workouts w WHERE w.user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(w.activity_name) LIKE ? OR LOWER(w.notes) LIKE ?) ");
            String wild = "%" + keyword.trim().toLowerCase() + "%";
            params.add(wild);
            params.add(wild);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND w.category_id = ? ");
            params.add(categoryId);
        }
        if (startDate != null) {
            sql.append("AND w.workout_date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND w.workout_date <= ? ");
            params.add(Date.valueOf(endDate));
        }

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting workouts", e);
            throw new DatabaseException("Failed to count workouts: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Map<String, Object> getUserWorkoutStats(int userId) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) AS total_count, " +
                "COALESCE(SUM(duration_minutes), 0) AS total_minutes, " +
                "COALESCE(SUM(calories_burned), 0) AS total_calories, " +
                "COALESCE(AVG(duration_minutes), 0) AS avg_duration, " +
                "COUNT(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 END) AS this_week_count, " +
                "COALESCE(SUM(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN duration_minutes ELSE 0 END), 0) AS this_week_minutes, " +
                "COALESCE(SUM(CASE WHEN workout_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN calories_burned ELSE 0 END), 0) AS this_week_calories " +
                "FROM workouts WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalCount", rs.getInt("total_count"));
                    stats.put("totalMinutes", rs.getLong("total_minutes"));
                    stats.put("totalCalories", rs.getLong("total_calories"));
                    stats.put("avgDuration", Math.round(rs.getDouble("avg_duration")));
                    stats.put("thisWeekCount", rs.getInt("this_week_count"));
                    stats.put("thisWeekMinutes", rs.getLong("this_week_minutes"));
                    stats.put("thisWeekCalories", rs.getLong("this_week_calories"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error computing user workout stats: {}", userId, e);
            throw new DatabaseException("Failed to compute stats: " + e.getMessage(), e);
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getWeeklyWorkoutTrends(int userId, int weeks) {
        List<Map<String, Object>> trends = new ArrayList<>();
        String sql = "SELECT workout_date, SUM(duration_minutes) AS total_minutes, SUM(calories_burned) AS total_calories, COUNT(*) as workout_count " +
                "FROM workouts WHERE user_id = ? AND workout_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY workout_date ORDER BY workout_date ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, weeks * 7);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> day = new HashMap<>();
                    day.put("date", rs.getDate("workout_date").toString());
                    day.put("minutes", rs.getInt("total_minutes"));
                    day.put("calories", rs.getInt("total_calories"));
                    day.put("count", rs.getInt("workout_count"));
                    trends.add(day);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving weekly trends", e);
            throw new DatabaseException("Failed to retrieve trends: " + e.getMessage(), e);
        }
        return trends;
    }

    @Override
    public List<Map<String, Object>> getIntensityBreakdown(int userId) {
        List<Map<String, Object>> breakdown = new ArrayList<>();
        String sql = "SELECT intensity, COUNT(*) AS count, SUM(calories_burned) AS calories FROM workouts " +
                "WHERE user_id = ? GROUP BY intensity";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("intensity", rs.getString("intensity"));
                    row.put("count", rs.getInt("count"));
                    row.put("calories", rs.getInt("calories"));
                    breakdown.add(row);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching intensity breakdown", e);
            throw new DatabaseException("Failed to fetch intensity breakdown: " + e.getMessage(), e);
        }
        return breakdown;
    }

    @Override
    public List<Map<String, Object>> getCategoryDistribution(int userId) {
        List<Map<String, Object>> dist = new ArrayList<>();
        String sql = "SELECT COALESCE(c.name, 'Other') AS category_name, COUNT(w.id) AS count, SUM(w.duration_minutes) as minutes " +
                "FROM workouts w LEFT JOIN workout_categories c ON w.category_id = c.id " +
                "WHERE w.user_id = ? GROUP BY c.name ORDER BY count DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("category", rs.getString("category_name"));
                    row.put("count", rs.getInt("count"));
                    row.put("minutes", rs.getInt("minutes"));
                    dist.add(row);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching category distribution", e);
            throw new DatabaseException("Failed to fetch category distribution: " + e.getMessage(), e);
        }
        return dist;
    }

    @Override
    public List<WorkoutCategory> findAllCategories() {
        List<WorkoutCategory> list = new ArrayList<>();
        String sql = "SELECT * FROM workout_categories ORDER BY name ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                WorkoutCategory cat = new WorkoutCategory();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                cat.setMetValue(rs.getBigDecimal("met_value"));
                cat.setIconName(rs.getString("icon_name"));
                cat.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(cat);
            }
        } catch (SQLException e) {
            logger.error("Error finding categories", e);
            throw new DatabaseException("Failed to fetch categories: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public int countTotalWorkoutsAllUsers() {
        String sql = "SELECT COUNT(*) FROM workouts";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting total workouts", e);
        }
        return 0;
    }

    @Override
    public long sumTotalCaloriesAllUsers() {
        String sql = "SELECT COALESCE(SUM(calories_burned), 0) FROM workouts";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            logger.error("Error summing total calories", e);
        }
        return 0;
    }

    @Override
    public long sumTotalMinutesAllUsers() {
        String sql = "SELECT COALESCE(SUM(duration_minutes), 0) FROM workouts";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            logger.error("Error summing total minutes", e);
        }
        return 0;
    }

    private Workout mapResultSetToWorkout(ResultSet rs) throws SQLException {
        Workout w = new Workout();
        w.setId(rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));

        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) w.setCategoryId(catId);

        try {
            w.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {
        }

        w.setActivityName(rs.getString("activity_name"));
        w.setWorkoutDate(rs.getDate("workout_date").toLocalDate());
        w.setDurationMinutes(rs.getInt("duration_minutes"));
        w.setIntensity(WorkoutIntensity.fromString(rs.getString("intensity")));
        w.setCaloriesBurned(rs.getInt("calories_burned"));
        w.setNotes(rs.getString("notes"));
        w.setCreatedAt(rs.getTimestamp("created_at"));
        return w;
    }
}
