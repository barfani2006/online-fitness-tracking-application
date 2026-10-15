package com.fitness.servlet;

import com.fitness.dao.ContentDAO;
import com.fitness.dao.GoalDAO;
import com.fitness.dao.WorkoutDAO;
import com.fitness.dao.impl.ContentDAOImpl;
import com.fitness.dao.impl.GoalDAOImpl;
import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.model.ContentCategory;
import com.fitness.model.FitnessContent;
import com.fitness.model.FitnessGoal;
import com.fitness.model.FitnessRecommendation;
import com.fitness.model.User;
import com.fitness.model.Workout;
import com.fitness.service.RecommendationService;
import com.fitness.service.impl.RuleBasedRecommendationService;
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

/**
 * Controller delivering personalized rule-based training guidance and curated educational articles.
 */
@WebServlet(name = "RecommendationServlet", urlPatterns = {"/recommendations"})
public class RecommendationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServlet.class);
    private final WorkoutDAO workoutDAO;
    private final GoalDAO goalDAO;
    private final ContentDAO contentDAO;
    private final RecommendationService recommendationService;

    public RecommendationServlet() {
        this(new WorkoutDAOImpl(), new GoalDAOImpl(), new ContentDAOImpl(), new RuleBasedRecommendationService());
    }

    public RecommendationServlet(WorkoutDAO workoutDAO, GoalDAO goalDAO, ContentDAO contentDAO, RecommendationService recommendationService) {
        this.workoutDAO = workoutDAO;
        this.goalDAO = goalDAO;
        this.contentDAO = contentDAO;
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
            List<Workout> recentWorkouts = workoutDAO.findRecentByUserId(userId, 10);
            List<FitnessGoal> activeGoals = goalDAO.findActiveByUserId(userId);

            // Compute personalized rule-based suggestions
            List<FitnessRecommendation> recommendations = recommendationService.generateRecommendations(user, recentWorkouts, activeGoals);

            // Fetch approved community educational content
            String categoryParam = req.getParameter("category");
            List<FitnessContent> articles;
            if (categoryParam != null && !categoryParam.isEmpty()) {
                ContentCategory cat = ContentCategory.fromString(categoryParam);
                articles = contentDAO.findApprovedByCategory(cat);
            } else {
                articles = contentDAO.findApprovedContent();
            }

            req.setAttribute("recommendations", recommendations);
            req.setAttribute("articles", articles);
            req.setAttribute("categories", ContentCategory.values());
            req.setAttribute("selectedCategory", categoryParam);
            req.setAttribute("disclaimer", FitnessRecommendation.DISCLAIMER);
            req.setAttribute("activeNav", "recommendations");
            req.setAttribute("pageTitle", "Recommendations - PulseFit");
            req.setAttribute("pageHeading", "Personalized Insights & Guidance");

            req.getRequestDispatcher("/WEB-INF/views/user/recommendations.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error generating recommendations", e);
            req.setAttribute("errorMessage", "Unable to load recommendations at this time.");
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        }
    }
}
