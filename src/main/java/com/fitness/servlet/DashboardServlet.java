package com.fitness.servlet;

import com.fitness.dao.ChallengeDAO;
import com.fitness.dao.GoalDAO;
import com.fitness.dao.WorkoutDAO;
import com.fitness.dao.impl.ChallengeDAOImpl;
import com.fitness.dao.impl.GoalDAOImpl;
import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.model.FitnessGoal;
import com.fitness.model.FitnessRecommendation;
import com.fitness.model.User;
import com.fitness.model.UserChallenge;
import com.fitness.model.Workout;
import com.fitness.service.RecommendationService;
import com.fitness.service.impl.RuleBasedRecommendationService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller preparing user dashboard overview, statistics, active goals, and recommendations.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);
    private final WorkoutDAO workoutDAO;
    private final GoalDAO goalDAO;
    private final ChallengeDAO challengeDAO;
    private final RecommendationService recommendationService;
    private final Gson gson = new Gson();

    public DashboardServlet() {
        this(new WorkoutDAOImpl(), new GoalDAOImpl(), new ChallengeDAOImpl(), new RuleBasedRecommendationService());
    }

    public DashboardServlet(WorkoutDAO workoutDAO, GoalDAO goalDAO, ChallengeDAO challengeDAO, RecommendationService recommendationService) {
        this.workoutDAO = workoutDAO;
        this.goalDAO = goalDAO;
        this.challengeDAO = challengeDAO;
        this.recommendationService = recommendationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int userId = user.getId();
            Map<String, Object> stats = workoutDAO.getUserWorkoutStats(userId);
            List<Workout> recentWorkouts = workoutDAO.findRecentByUserId(userId, 5);
            List<FitnessGoal> activeGoals = goalDAO.findActiveByUserId(userId);
            List<UserChallenge> activeChallenges = challengeDAO.findUserActiveChallenges(userId);
            List<FitnessRecommendation> recommendations = recommendationService.generateRecommendations(user, recentWorkouts, activeGoals);
            List<Map<String, Object>> weeklyTrends = workoutDAO.getWeeklyWorkoutTrends(userId, 2);

            req.setAttribute("stats", stats);
            req.setAttribute("recentWorkouts", recentWorkouts);
            req.setAttribute("activeGoals", activeGoals);
            req.setAttribute("activeChallenges", activeChallenges);
            req.setAttribute("recommendations", recommendations);
            req.setAttribute("weeklyTrendsJson", gson.toJson(weeklyTrends));
            req.setAttribute("activeNav", "dashboard");
            req.setAttribute("pageTitle", "User Dashboard - PulseFit");
            req.setAttribute("pageHeading", "Overview & Recent Activity");

            // Transfer flash notifications
            String flashSuccess = (String) session.getAttribute("flashSuccess");
            if (flashSuccess != null) {
                req.setAttribute("successMessage", flashSuccess);
                session.removeAttribute("flashSuccess");
            }

            req.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error populating user dashboard", e);
            req.setAttribute("errorMessage", "Unable to load dashboard data. Please check database connection.");
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        }
    }
}
