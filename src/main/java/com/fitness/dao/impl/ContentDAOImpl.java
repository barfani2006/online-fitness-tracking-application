package com.fitness.dao.impl;

import com.fitness.dao.ContentDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.ContentCategory;
import com.fitness.model.ContentStatus;
import com.fitness.model.FitnessContent;
import com.fitness.util.ConnectionProvider;
import com.fitness.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ContentDAO with prepared statements.
 */
public class ContentDAOImpl implements ContentDAO {

    private static final Logger logger = LoggerFactory.getLogger(ContentDAOImpl.class);
    private final ConnectionProvider connectionProvider;

    public ContentDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public ContentDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<FitnessContent> findById(Integer id) {
        String sql = "SELECT c.*, u.full_name AS author_name FROM fitness_content c " +
                "JOIN users u ON c.author_id = u.id WHERE c.id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToContent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding content by id: {}", id, e);
            throw new DatabaseException("Failed to find content: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<FitnessContent> findAll() {
        List<FitnessContent> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS author_name FROM fitness_content c " +
                "JOIN users u ON c.author_id = u.id ORDER BY c.created_at DESC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToContent(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all content", e);
            throw new DatabaseException("Failed to retrieve content: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<FitnessContent> findApprovedContent() {
        List<FitnessContent> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS author_name FROM fitness_content c " +
                "JOIN users u ON c.author_id = u.id WHERE c.status = 'APPROVED' ORDER BY c.created_at DESC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToContent(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching approved content", e);
            throw new DatabaseException("Failed to retrieve approved articles: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<FitnessContent> findApprovedByCategory(ContentCategory category) {
        List<FitnessContent> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS author_name FROM fitness_content c " +
                "JOIN users u ON c.author_id = u.id WHERE c.status = 'APPROVED' AND c.category = ? ORDER BY c.created_at DESC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToContent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching content by category", e);
            throw new DatabaseException("Failed to filter content: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<FitnessContent> findPendingContent() {
        return findAllWithStatus(ContentStatus.PENDING);
    }

    @Override
    public List<FitnessContent> findAllWithStatus(ContentStatus status) {
        List<FitnessContent> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS author_name FROM fitness_content c " +
                "JOIN users u ON c.author_id = u.id WHERE c.status = ? ORDER BY c.created_at ASC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToContent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching content by status: {}", status, e);
            throw new DatabaseException("Failed to retrieve content: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public FitnessContent save(FitnessContent c) {
        String sql = "INSERT INTO fitness_content (title, content, category, author_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getTitle().trim());
            stmt.setString(2, c.getContent().trim());
            stmt.setString(3, c.getCategory().name());
            stmt.setInt(4, c.getAuthorId());
            stmt.setString(5, c.getStatus().name());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) c.setId(gk.getInt(1));
            }
            return c;
        } catch (SQLException e) {
            logger.error("Error saving content", e);
            throw new DatabaseException("Failed to save content: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessContent c) {
        String sql = "UPDATE fitness_content SET title = ?, content = ?, category = ?, status = ?, rejection_reason = ? WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle().trim());
            stmt.setString(2, c.getContent().trim());
            stmt.setString(3, c.getCategory().name());
            stmt.setString(4, c.getStatus().name());
            stmt.setString(5, c.getRejectionReason());
            stmt.setInt(6, c.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating content: {}", c.getId(), e);
            throw new DatabaseException("Failed to update content: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int contentId, ContentStatus status, String rejectionReason) {
        String sql = "UPDATE fitness_content SET status = ?, rejection_reason = ? WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, rejectionReason);
            stmt.setInt(3, contentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating content status: {}", contentId, e);
            throw new DatabaseException("Failed to update status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM fitness_content WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting content: {}", id, e);
            throw new DatabaseException("Failed to delete content: " + e.getMessage(), e);
        }
    }

    @Override
    public int countPendingContent() {
        String sql = "SELECT COUNT(*) FROM fitness_content WHERE status = 'PENDING'";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting pending content", e);
        }
        return 0;
    }

    @Override
    public int countApprovedContent() {
        String sql = "SELECT COUNT(*) FROM fitness_content WHERE status = 'APPROVED'";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting approved content", e);
        }
        return 0;
    }

    private FitnessContent mapResultSetToContent(ResultSet rs) throws SQLException {
        FitnessContent c = new FitnessContent();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setContent(rs.getString("content"));
        c.setCategory(ContentCategory.fromString(rs.getString("category")));
        c.setAuthorId(rs.getInt("author_id"));
        c.setStatus(ContentStatus.fromString(rs.getString("status")));
        c.setRejectionReason(rs.getString("rejection_reason"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            c.setAuthorName(rs.getString("author_name"));
        } catch (SQLException ignored) {
        }
        return c;
    }
}
