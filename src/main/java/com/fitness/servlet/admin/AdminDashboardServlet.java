package com.fitness.servlet.admin;

import com.fitness.dao.ChallengeDAO;
import com.fitness.dao.ContentDAO;
import com.fitness.dao.GoalDAO;
import com.fitness.dao.UserDAO;
import com.fitness.dao.WorkoutDAO;
import com.fitness.dao.impl.ChallengeDAOImpl;
import com.fitness.dao.impl.ContentDAOImpl;
import com.fitness.dao.impl.GoalDAOImpl;
import com.fitness.dao.impl.UserDAOImpl;
import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.listener.SessionListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for Administrator Dashboard presenting platform-wide activity statistics, user volume, and pending tasks.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);
    private final UserDAO userDAO;
    private final WorkoutDAO workoutDAO;
    private final GoalDAO goalDAO;
    private final ChallengeDAO challengeDAO;
    private final ContentDAO contentDAO;

    public AdminDashboardServlet() {
        this(new UserDAOImpl(), new WorkoutDAOImpl(), new GoalDAOImpl(), new ChallengeDAOImpl(), new ContentDAOImpl());
    }

    public AdminDashboardServlet(UserDAO userDAO, WorkoutDAO workoutDAO, GoalDAO goalDAO,
                                 ChallengeDAO challengeDAO, ContentDAO contentDAO) {
        this.userDAO = userDAO;
        this.workoutDAO = workoutDAO;
        this.goalDAO = goalDAO;
        this.challengeDAO = challengeDAO;
        this.contentDAO = contentDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userDAO.countTotalUsers());
            stats.put("totalWorkouts", workoutDAO.countTotalWorkoutsAllUsers());
            stats.put("totalCalories", workoutDAO.sumTotalCaloriesAllUsers());
            stats.put("totalMinutes", workoutDAO.sumTotalMinutesAllUsers());
            stats.put("activeGoals", goalDAO.countActiveGoalsAllUsers());
            stats.put("completedGoals", goalDAO.countCompletedGoalsAllUsers());
            stats.put("activeChallenges", challengeDAO.countActiveChallenges());
            stats.put("totalChallengeEnrollments", challengeDAO.countTotalParticipationsAllUsers());
            stats.put("pendingContent", contentDAO.countPendingContent());
            stats.put("activeSessions", SessionListener.getActiveSessionsCount());

            req.setAttribute("stats", stats);
            req.setAttribute("recentUsers", userDAO.searchUsers(null, null, null, 0, 5));
            req.setAttribute("pendingArticles", contentDAO.findPendingContent());
            req.setAttribute("activeNav", "admin-dashboard");
            req.setAttribute("pageTitle", "Admin Overview - PulseFit");
            req.setAttribute("pageHeading", "Platform Statistics & Overview");

            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error loading admin dashboard", e);
            req.setAttribute("errorMessage", "Failed to load administrative overview statistics.");
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        }
    }
}
