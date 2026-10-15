package com.fitness.dao.impl;

import com.fitness.dao.GoalDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.FitnessGoal;
import com.fitness.model.GoalStatus;
import com.fitness.model.GoalType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of GoalDAO utilizing prepared statements and transaction safety.
 */
public class GoalDAOImpl implements GoalDAO {

    private static final Logger logger = LoggerFactory.getLogger(GoalDAOImpl.class);
    private final DBConnectionManager dbManager;

    public GoalDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public GoalDAOImpl(DBConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<FitnessGoal> findById(Integer id) {
        String sql = "SELECT * FROM fitness_goals WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding goal by id: {}", id, e);
            throw new DatabaseException("Failed to find goal: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<FitnessGoal> findAll() {
        List<FitnessGoal> list = new ArrayList<>();
        String sql = "SELECT * FROM fitness_goals ORDER BY target_date ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToGoal(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all goals", e);
            throw new DatabaseException("Failed to fetch goals: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<FitnessGoal> findByUserId(int userId) {
        List<FitnessGoal> list = new ArrayList<>();
        String sql = "SELECT * FROM fitness_goals WHERE user_id = ? ORDER BY status ASC, target_date ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving goals for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch user goals: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<FitnessGoal> findActiveByUserId(int userId) {
        List<FitnessGoal> list = new ArrayList<>();
        String sql = "SELECT * FROM fitness_goals WHERE user_id = ? AND status = 'ACTIVE' ORDER BY target_date ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving active goals for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch active goals: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public FitnessGoal save(FitnessGoal goal) {
        String sql = "INSERT INTO fitness_goals (user_id, title, goal_type, target_value, current_value, unit, start_date, target_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getTitle().trim());
            stmt.setString(3, goal.getGoalType().name());
            stmt.setBigDecimal(4, goal.getTargetValue());
            stmt.setBigDecimal(5, goal.getCurrentValue() != null ? goal.getCurrentValue() : BigDecimal.ZERO);
            stmt.setString(6, goal.getUnit());
            stmt.setDate(7, Date.valueOf(goal.getStartDate()));
            stmt.setDate(8, Date.valueOf(goal.getTargetDate()));
            stmt.setString(9, goal.getStatus().name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Saving fitness goal failed, no rows affected.");
            }
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    goal.setId(gk.getInt(1));
                }
            }
            return goal;
        } catch (SQLException e) {
            logger.error("Error inserting fitness goal", e);
            throw new DatabaseException("Failed to create fitness goal: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessGoal goal) {
        String sql = "UPDATE fitness_goals SET title = ?, goal_type = ?, target_value = ?, current_value = ?, " +
                "unit = ?, target_date = ?, status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, goal.getTitle().trim());
            stmt.setString(2, goal.getGoalType().name());
            stmt.setBigDecimal(3, goal.getTargetValue());
            stmt.setBigDecimal(4, goal.getCurrentValue());
            stmt.setString(5, goal.getUnit());
            stmt.setDate(6, Date.valueOf(goal.getTargetDate()));
            stmt.setString(7, goal.getStatus().name());
            stmt.setInt(8, goal.getId());
            stmt.setInt(9, goal.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal: {}", goal.getId(), e);
            throw new DatabaseException("Failed to update goal: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateProgress(int goalId, int userId, BigDecimal newCurrentValue, GoalStatus newStatus) {
        String sql = "UPDATE fitness_goals SET current_value = ?, status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newCurrentValue);
            stmt.setString(2, newStatus.name());
            stmt.setInt(3, goalId);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal progress: {}", goalId, e);
            throw new DatabaseException("Failed to update goal progress: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int goalId, int userId, GoalStatus newStatus) {
        String sql = "UPDATE fitness_goals SET status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, goalId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating goal status: {}", goalId, e);
            throw new DatabaseException("Failed to update goal status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM fitness_goals WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting goal: {}", id, e);
            throw new DatabaseException("Failed to delete goal: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteByIdAndUserId(int goalId, int userId) {
        String sql = "DELETE FROM fitness_goals WHERE id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting goal: {} for user: {}", goalId, userId, e);
            throw new DatabaseException("Failed to delete goal: " + e.getMessage(), e);
        }
    }

    @Override
    public int countCompletedGoalsAllUsers() {
        String sql = "SELECT COUNT(*) FROM fitness_goals WHERE status = 'COMPLETED'";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting completed goals", e);
        }
        return 0;
    }

    @Override
    public int countActiveGoalsAllUsers() {
        String sql = "SELECT COUNT(*) FROM fitness_goals WHERE status = 'ACTIVE'";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting active goals", e);
        }
        return 0;
    }

    private FitnessGoal mapResultSetToGoal(ResultSet rs) throws SQLException {
        FitnessGoal goal = new FitnessGoal();
        goal.setId(rs.getInt("id"));
        goal.setUserId(rs.getInt("user_id"));
        goal.setTitle(rs.getString("title"));
        goal.setGoalType(GoalType.fromString(rs.getString("goal_type")));
        goal.setTargetValue(rs.getBigDecimal("target_value"));
        goal.setCurrentValue(rs.getBigDecimal("current_value"));
        goal.setUnit(rs.getString("unit"));
        goal.setStartDate(rs.getDate("start_date").toLocalDate());
        goal.setTargetDate(rs.getDate("target_date").toLocalDate());
        goal.setStatus(GoalStatus.fromString(rs.getString("status")));
        goal.setCreatedAt(rs.getTimestamp("created_at"));
        goal.setUpdatedAt(rs.getTimestamp("updated_at"));
        return goal;
    }
}
