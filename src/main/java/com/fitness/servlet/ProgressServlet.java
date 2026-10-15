package com.fitness.servlet;

import com.fitness.model.User;
import com.fitness.model.UserProgressSummary;
import com.fitness.service.ProgressService;
import com.fitness.service.impl.ProgressServiceImpl;
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

/**
 * Controller preparing interactive workout progress charts, distribution breakdowns, and frequency metrics.
 */
@WebServlet(name = "ProgressServlet", urlPatterns = {"/workouts/progress"})
public class ProgressServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProgressServlet.class);
    private final ProgressService progressService;
    private final Gson gson = new Gson();

    public ProgressServlet() {
        this(new ProgressServiceImpl());
    }

    public ProgressServlet(ProgressService progressService) {
        this.progressService = progressService;
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
            UserProgressSummary progress = progressService.calculateUserProgress(user.getId());

            req.setAttribute("progress", progress);
            req.setAttribute("dailyTrendsJson", gson.toJson(progress.getDailyTrends()));
            req.setAttribute("intensityDistJson", gson.toJson(progress.getIntensityDistribution()));
            req.setAttribute("categoryDistJson", gson.toJson(progress.getCategoryDistribution()));
            req.setAttribute("activeNav", "progress");
            req.setAttribute("pageTitle", "Progress Analytics - PulseFit");
            req.setAttribute("pageHeading", "Workout History & Progress Analytics");

            req.getRequestDispatcher("/WEB-INF/views/workout/progress.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error loading progress analytics", e);
            req.setAttribute("errorMessage", "Unable to compute fitness progress analytics.");
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        }
    }
}
