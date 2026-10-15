package com.fitness.util;

import com.fitness.dao.ChallengeDAO;
import com.fitness.dao.GoalDAO;
import com.fitness.dao.WorkoutDAO;
import com.fitness.dao.impl.ChallengeDAOImpl;
import com.fitness.dao.impl.GoalDAOImpl;
import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.model.FitnessGoal;
import com.fitness.model.UserChallenge;
import com.fitness.model.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Diagnostic test verifying DBConnectionManager initialization and Dashboard queries.
 */
class DBConnectionManagerTest {

    @Test
    @DisplayName("Verify connection and Dashboard queries work seamlessly")
    void testDashboardQueries() {
        Connection conn = DBConnectionManager.getInstance().getConnection();
        assertNotNull(conn, "Connection must not be null");

        WorkoutDAO workoutDAO = new WorkoutDAOImpl(DBConnectionManager.getInstance());
        GoalDAO goalDAO = new GoalDAOImpl(DBConnectionManager.getInstance());
        ChallengeDAO challengeDAO = new ChallengeDAOImpl(DBConnectionManager.getInstance());

        // Test user 3 (alex.miller)
        int userId = 3;

        Map<String, Object> stats = assertDoesNotThrow(() -> workoutDAO.getUserWorkoutStats(userId));
        assertNotNull(stats);

        List<Workout> recentWorkouts = assertDoesNotThrow(() -> workoutDAO.findRecentByUserId(userId, 5));
        assertNotNull(recentWorkouts);

        List<FitnessGoal> activeGoals = assertDoesNotThrow(() -> goalDAO.findActiveByUserId(userId));
        assertNotNull(activeGoals);

        List<UserChallenge> activeChallenges = assertDoesNotThrow(() -> challengeDAO.findUserActiveChallenges(userId));
        assertNotNull(activeChallenges);

        List<Map<String, Object>> weeklyTrends = assertDoesNotThrow(() -> workoutDAO.getWeeklyWorkoutTrends(userId, 2));
        assertNotNull(weeklyTrends);
    }
}
