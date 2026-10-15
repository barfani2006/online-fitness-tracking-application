package com.fitness.dao.impl;

import com.fitness.dao.AuditLogDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.AuditLog;
import com.fitness.util.ConnectionProvider;
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
 * JDBC implementation of AuditLogDAO with prepared statements.
 */
public class AuditLogDAOImpl implements AuditLogDAO {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogDAOImpl.class);
    private final ConnectionProvider connectionProvider;

    public AuditLogDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public AuditLogDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<AuditLog> findById(Integer id) {
        String sql = "SELECT a.*, u.email AS user_email FROM audit_logs a " +
                "LEFT JOIN users u ON a.user_id = u.id WHERE a.id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToAuditLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding audit log by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<AuditLog> findAll() {
        return findRecent(100);
    }

    @Override
    public List<AuditLog> findRecent(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.email AS user_email FROM audit_logs a " +
                "LEFT JOIN users u ON a.user_id = u.id ORDER BY a.created_at DESC LIMIT ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving recent audit logs", e);
            throw new DatabaseException("Failed to fetch audit logs: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<AuditLog> findByAction(String action, int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.email AS user_email FROM audit_logs a " +
                "LEFT JOIN users u ON a.user_id = u.id WHERE a.action = ? ORDER BY a.created_at DESC LIMIT ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAuditLog(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving audit logs by action", e);
        }
        return list;
    }

    @Override
    public AuditLog save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, ip_address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) stmt.setInt(1, log.getUserId());
            else stmt.setNull(1, Types.INTEGER);

            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getEntityType());

            if (log.getEntityId() != null) stmt.setInt(4, log.getEntityId());
            else stmt.setNull(4, Types.INTEGER);

            stmt.setString(5, log.getDetails());
            stmt.setString(6, log.getIpAddress());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) log.setId(gk.getInt(1));
            }
            return log;
        } catch (SQLException e) {
            logger.error("Error saving audit log", e);
            throw new DatabaseException("Failed to save audit log: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(AuditLog entity) {
        throw new UnsupportedOperationException("Audit logs are immutable and cannot be updated.");
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM audit_logs WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting audit log: {}", id, e);
            throw new DatabaseException("Failed to delete audit log: " + e.getMessage(), e);
        }
    }

    @Override
    public int countTotalLogs() {
        String sql = "SELECT COUNT(*) FROM audit_logs";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting audit logs", e);
        }
        return 0;
    }

    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getInt("id"));

        int uId = rs.getInt("user_id");
        if (!rs.wasNull()) log.setUserId(uId);

        try {
            log.setUserEmail(rs.getString("user_email"));
        } catch (SQLException ignored) {
        }

        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));

        int entId = rs.getInt("entity_id");
        if (!rs.wasNull()) log.setEntityId(entId);

        log.setDetails(rs.getString("details"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
