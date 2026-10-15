package com.fitness.dao.impl;

import com.fitness.dao.ChallengeDAO;
import com.fitness.exception.DatabaseException;
import com.fitness.model.ChallengeMetric;
import com.fitness.model.FitnessChallenge;
import com.fitness.model.UserChallenge;
import com.fitness.model.UserChallengeStatus;
import com.fitness.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ChallengeDAO supporting transactions and participant progress.
 */
public class ChallengeDAOImpl implements ChallengeDAO {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeDAOImpl.class);
    private final DBConnectionManager dbManager;

    public ChallengeDAOImpl() {
        this(DBConnectionManager.getInstance());
    }

    public ChallengeDAOImpl(DBConnectionManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<FitnessChallenge> findById(Integer id) {
        String sql = "SELECT c.*, COUNT(uc.id) AS participant_count FROM fitness_challenges c " +
                "LEFT JOIN user_challenges uc ON c.id = uc.challenge_id WHERE c.id = ? GROUP BY c.id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FitnessChallenge challenge = mapResultSetToChallenge(rs);
                    challenge.setParticipantCount(rs.getInt("participant_count"));
                    return Optional.of(challenge);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding challenge by id: {}", id, e);
            throw new DatabaseException("Failed to find challenge: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<FitnessChallenge> findAll() {
        List<FitnessChallenge> list = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(uc.id) AS participant_count FROM fitness_challenges c " +
                "LEFT JOIN user_challenges uc ON c.id = uc.challenge_id GROUP BY c.id ORDER BY c.start_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                FitnessChallenge c = mapResultSetToChallenge(rs);
                c.setParticipantCount(rs.getInt("participant_count"));
                list.add(c);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving challenges", e);
            throw new DatabaseException("Failed to fetch challenges: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<FitnessChallenge> findAllWithUserStatus(int userId) {
        List<FitnessChallenge> list = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "(SELECT COUNT(*) FROM user_challenges WHERE challenge_id = c.id) AS participant_count, " +
                "uc.id AS uc_id, uc.current_progress, uc.status AS user_status " +
                "FROM fitness_challenges c " +
                "LEFT JOIN user_challenges uc ON c.id = uc.challenge_id AND uc.user_id = ? " +
                "ORDER BY c.end_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FitnessChallenge c = mapResultSetToChallenge(rs);
                    c.setParticipantCount(rs.getInt("participant_count"));
                    int ucId = rs.getInt("uc_id");
                    if (!rs.wasNull()) {
                        c.setUserJoined(true);
                        c.setUserProgress(rs.getInt("current_progress"));
                        c.setUserStatus(rs.getString("user_status"));
                    } else {
                        c.setUserJoined(false);
                    }
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching challenges with user status for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch challenges: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<UserChallenge> findUserActiveChallenges(int userId) {
        List<UserChallenge> list = new ArrayList<>();
        String sql = "SELECT uc.*, c.title, c.description, c.target_metric, c.target_goal, c.badge_name " +
                "FROM user_challenges uc " +
                "JOIN fitness_challenges c ON uc.challenge_id = c.id " +
                "WHERE uc.user_id = ? AND uc.status = 'IN_PROGRESS' ORDER BY uc.joined_at DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUserChallenge(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching active challenges for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch active challenges: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<UserChallenge> findUserChallengeHistory(int userId) {
        List<UserChallenge> list = new ArrayList<>();
        String sql = "SELECT uc.*, c.title, c.description, c.target_metric, c.target_goal, c.badge_name " +
                "FROM user_challenges uc " +
                "JOIN fitness_challenges c ON uc.challenge_id = c.id " +
                "WHERE uc.user_id = ? ORDER BY uc.joined_at DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUserChallenge(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching challenge history for user: {}", userId, e);
            throw new DatabaseException("Failed to fetch challenge history: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean isUserEnrolled(int userId, int challengeId) {
        String sql = "SELECT 1 FROM user_challenges WHERE user_id = ? AND challenge_id = ? LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, challengeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking enrollment: user={}, challenge={}", userId, challengeId, e);
            throw new DatabaseException("Failed to check enrollment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean joinChallenge(int userId, int challengeId) {
        String sql = "INSERT INTO user_challenges (user_id, challenge_id, current_progress, status) " +
                "VALUES (?, ?, 0, 'IN_PROGRESS') " +
                "ON DUPLICATE KEY UPDATE status = 'IN_PROGRESS'";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, challengeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error joining challenge: user={}, challenge={}", userId, challengeId, e);
            throw new DatabaseException("Failed to join challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean leaveChallenge(int userId, int challengeId) {
        String sql = "DELETE FROM user_challenges WHERE user_id = ? AND challenge_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, challengeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error leaving challenge: user={}, challenge={}", userId, challengeId, e);
            throw new DatabaseException("Failed to leave challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateChallengeProgress(int userId, int challengeId, int addedProgress) {
        String selectSql = "SELECT uc.current_progress, c.target_goal FROM user_challenges uc " +
                "JOIN fitness_challenges c ON uc.challenge_id = c.id " +
                "WHERE uc.user_id = ? AND uc.challenge_id = ? AND uc.status = 'IN_PROGRESS' FOR UPDATE";
        String updateSql = "UPDATE user_challenges SET current_progress = ?, status = ?, completed_at = ? WHERE user_id = ? AND challenge_id = ?";

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false); // Begin JDBC Transaction

            int newProg = 0;
            int target = 0;
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, challengeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int current = rs.getInt("current_progress");
                        target = rs.getInt("target_goal");
                        newProg = current + addedProgress;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            boolean completed = newProg >= target;
            String status = completed ? UserChallengeStatus.COMPLETED.name() : UserChallengeStatus.IN_PROGRESS.name();
            Timestamp completedAt = completed ? new Timestamp(System.currentTimeMillis()) : null;

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, newProg);
                stmt.setString(2, status);
                stmt.setTimestamp(3, completedAt);
                stmt.setInt(4, userId);
                stmt.setInt(5, challengeId);
                stmt.executeUpdate();
            }

            conn.commit(); // Commit JDBC Transaction
            return true;
        } catch (SQLException e) {
            DBConnectionManager.rollbackQuietly(conn);
            logger.error("Error updating challenge progress in transaction", e);
            throw new DatabaseException("Failed to update challenge progress: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    @Override
    public FitnessChallenge save(FitnessChallenge c) {
        String sql = "INSERT INTO fitness_challenges (title, description, target_metric, target_goal, duration_days, badge_name, start_date, end_date, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getTitle().trim());
            stmt.setString(2, c.getDescription().trim());
            stmt.setString(3, c.getTargetMetric().name());
            stmt.setInt(4, c.getTargetGoal());
            stmt.setInt(5, c.getDurationDays());
            stmt.setString(6, c.getBadgeName());
            stmt.setDate(7, Date.valueOf(c.getStartDate()));
            stmt.setDate(8, Date.valueOf(c.getEndDate()));
            if (c.getCreatedBy() != null) stmt.setInt(9, c.getCreatedBy());
            else stmt.setNull(9, java.sql.Types.INTEGER);

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) c.setId(gk.getInt(1));
            }
            return c;
        } catch (SQLException e) {
            logger.error("Error saving challenge", e);
            throw new DatabaseException("Failed to create challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(FitnessChallenge c) {
        String sql = "UPDATE fitness_challenges SET title = ?, description = ?, target_metric = ?, target_goal = ?, " +
                "duration_days = ?, badge_name = ?, start_date = ?, end_date = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle().trim());
            stmt.setString(2, c.getDescription().trim());
            stmt.setString(3, c.getTargetMetric().name());
            stmt.setInt(4, c.getTargetGoal());
            stmt.setInt(5, c.getDurationDays());
            stmt.setString(6, c.getBadgeName());
            stmt.setDate(7, Date.valueOf(c.getStartDate()));
            stmt.setDate(8, Date.valueOf(c.getEndDate()));
            stmt.setInt(9, c.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating challenge: {}", c.getId(), e);
            throw new DatabaseException("Failed to update challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM fitness_challenges WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting challenge: {}", id, e);
            throw new DatabaseException("Failed to delete challenge: " + e.getMessage(), e);
        }
    }

    @Override
    public int countActiveChallenges() {
        String sql = "SELECT COUNT(*) FROM fitness_challenges WHERE CURDATE() BETWEEN start_date AND end_date";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting active challenges", e);
        }
        return 0;
    }

    @Override
    public int countTotalParticipationsAllUsers() {
        String sql = "SELECT COUNT(*) FROM user_challenges";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting total challenge participations", e);
        }
        return 0;
    }

    private FitnessChallenge mapResultSetToChallenge(ResultSet rs) throws SQLException {
        FitnessChallenge c = new FitnessChallenge();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setTargetMetric(ChallengeMetric.fromString(rs.getString("target_metric")));
        c.setTargetGoal(rs.getInt("target_goal"));
        c.setDurationDays(rs.getInt("duration_days"));
        c.setBadgeName(rs.getString("badge_name"));
        c.setStartDate(rs.getDate("start_date").toLocalDate());
        c.setEndDate(rs.getDate("end_date").toLocalDate());

        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) c.setCreatedBy(createdBy);

        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    private UserChallenge mapResultSetToUserChallenge(ResultSet rs) throws SQLException {
        UserChallenge uc = new UserChallenge();
        uc.setId(rs.getInt("id"));
        uc.setUserId(rs.getInt("user_id"));
        uc.setChallengeId(rs.getInt("challenge_id"));
        uc.setJoinedAt(rs.getTimestamp("joined_at"));
        uc.setCurrentProgress(rs.getInt("current_progress"));
        uc.setStatus(UserChallengeStatus.fromString(rs.getString("status")));
        uc.setCompletedAt(rs.getTimestamp("completed_at"));

        try {
            uc.setChallengeTitle(rs.getString("title"));
            uc.setChallengeDescription(rs.getString("description"));
            uc.setTargetMetric(ChallengeMetric.fromString(rs.getString("target_metric")));
            uc.setTargetGoal(rs.getInt("target_goal"));
            uc.setBadgeName(rs.getString("badge_name"));
        } catch (SQLException ignored) {
        }
        return uc;
    }
}
