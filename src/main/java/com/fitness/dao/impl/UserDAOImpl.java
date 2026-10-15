package com.fitness.dao.impl;

import com.fitness.dao.UserDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.FitnessLevel;
import com.fitness.model.Gender;
import com.fitness.model.User;
import com.fitness.model.UserRole;
import com.fitness.model.UserStatus;
import com.fitness.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDAO utilizing prepared statements and connection pooling.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private final DBConnectionManager dbManager;

    public UserDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public UserDAOImpl(DBConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new DatabaseException("Failed to retrieve user: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
            throw new DatabaseException("Failed to find user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean emailExists(String email) {
        if (email == null) return false;
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking email existence: {}", email, e);
            throw new DatabaseException("Failed to verify email existence: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all users", e);
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, status, age, gender, weight_kg, height_cm, fitness_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getStatus().name());

            if (user.getAge() != null) stmt.setInt(6, user.getAge());
            else stmt.setNull(6, Types.INTEGER);

            stmt.setString(7, user.getGender() != null ? user.getGender().name() : Gender.PREFER_NOT_TO_SAY.name());

            if (user.getWeightKg() != null) stmt.setBigDecimal(8, user.getWeightKg());
            else stmt.setNull(8, Types.DECIMAL);

            if (user.getHeightCm() != null) stmt.setBigDecimal(9, user.getHeightCm());
            else stmt.setNull(9, Types.DECIMAL);

            stmt.setString(10, user.getFitnessLevel() != null ? user.getFitnessLevel().name() : FitnessLevel.BEGINNER.name());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error creating user: {}", user.getEmail(), e);
            throw new DatabaseException("Failed to register user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, role = ?, status = ?, age = ?, gender = ?, " +
                "weight_kg = ?, height_cm = ?, fitness_level = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getStatus().name());

            if (user.getAge() != null) stmt.setInt(5, user.getAge());
            else stmt.setNull(5, Types.INTEGER);

            stmt.setString(6, user.getGender() != null ? user.getGender().name() : Gender.PREFER_NOT_TO_SAY.name());

            if (user.getWeightKg() != null) stmt.setBigDecimal(7, user.getWeightKg());
            else stmt.setNull(7, Types.DECIMAL);

            if (user.getHeightCm() != null) stmt.setBigDecimal(8, user.getHeightCm());
            else stmt.setNull(8, Types.DECIMAL);

            stmt.setString(9, user.getFitnessLevel() != null ? user.getFitnessLevel().name() : FitnessLevel.BEGINNER.name());
            stmt.setInt(10, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getId(), e);
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, age = ?, gender = ?, weight_kg = ?, height_cm = ?, fitness_level = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());

            if (user.getAge() != null) stmt.setInt(2, user.getAge());
            else stmt.setNull(2, Types.INTEGER);

            stmt.setString(3, user.getGender() != null ? user.getGender().name() : Gender.PREFER_NOT_TO_SAY.name());

            if (user.getWeightKg() != null) stmt.setBigDecimal(4, user.getWeightKg());
            else stmt.setNull(4, Types.DECIMAL);

            if (user.getHeightCm() != null) stmt.setBigDecimal(5, user.getHeightCm());
            else stmt.setNull(5, Types.DECIMAL);

            stmt.setString(6, user.getFitnessLevel() != null ? user.getFitnessLevel().name() : FitnessLevel.BEGINNER.name());
            stmt.setInt(7, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user profile: {}", user.getId(), e);
            throw new DatabaseException("Failed to update profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePassword(Integer userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password for user: {}", userId, e);
            throw new DatabaseException("Failed to update password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Integer userId, UserStatus status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating status for user: {}", userId, e);
            throw new DatabaseException("Failed to update user status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user: {}", id, e);
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> searchUsers(String query, UserRole role, UserStatus status, int offset, int limit) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(full_name) LIKE ? OR LOWER(email) LIKE ?) ");
            String wild = "%" + query.trim().toLowerCase() + "%";
            params.add(wild);
            params.add(wild);
        }
        if (role != null) {
            sql.append("AND role = ? ");
            params.add(role.name());
        }
        if (status != null) {
            sql.append("AND status = ? ");
            params.add(status.name());
        }
        sql.append("ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching users", e);
            throw new DatabaseException("Failed to search users: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public int countUsers(String query, UserRole role, UserStatus status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(full_name) LIKE ? OR LOWER(email) LIKE ?) ");
            String wild = "%" + query.trim().toLowerCase() + "%";
            params.add(wild);
            params.add(wild);
        }
        if (role != null) {
            sql.append("AND role = ? ");
            params.add(role.name());
        }
        if (status != null) {
            sql.append("AND status = ? ");
            params.add(status.name());
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
            logger.error("Error counting users", e);
            throw new DatabaseException("Failed to count users: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int countTotalUsers() {
        return countUsers(null, null, null);
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.fromString(rs.getString("role")));
        user.setStatus(UserStatus.fromString(rs.getString("status")));

        int age = rs.getInt("age");
        if (!rs.wasNull()) user.setAge(age);

        user.setGender(Gender.fromString(rs.getString("gender")));
        user.setWeightKg(rs.getBigDecimal("weight_kg"));
        user.setHeightCm(rs.getBigDecimal("height_cm"));
        user.setFitnessLevel(FitnessLevel.fromString(rs.getString("fitness_level")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
