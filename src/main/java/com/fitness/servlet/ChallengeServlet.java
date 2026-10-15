package com.fitness.servlet;

import com.fitness.dao.ChallengeDAO;
import com.fitness.dao.impl.ChallengeDAOImpl;
import com.fitness.model.FitnessChallenge;
import com.fitness.model.User;
import com.fitness.model.UserChallenge;
import com.fitness.util.CsrfUtil;
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
 * Controller managing community challenges, browsing, joining, and leaving challenges.
 */
@WebServlet(name = "ChallengeServlet", urlPatterns = {"/challenges", "/challenges/*"})
public class ChallengeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeServlet.class);
    private final ChallengeDAO challengeDAO;

    public ChallengeServlet() {
        this(new ChallengeDAOImpl());
    }

    public ChallengeServlet(ChallengeDAO challengeDAO) {
        this.challengeDAO = challengeDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String view = req.getParameter("view");
        if ("history".equalsIgnoreCase(view)) {
            showChallengeHistory(req, resp, user.getId());
            return;
        }

        List<FitnessChallenge> challenges = challengeDAO.findAllWithUserStatus(user.getId());
        req.setAttribute("challenges", challenges);
        req.setAttribute("activeNav", "challenges");
        req.setAttribute("pageTitle", "Challenges - PulseFit");
        req.setAttribute("pageHeading", "Community Fitness Challenges");

        if (session != null) {
            String flashSuccess = (String) session.getAttribute("flashSuccess");
            if (flashSuccess != null) {
                req.setAttribute("successMessage", flashSuccess);
                session.removeAttribute("flashSuccess");
            }
            String flashError = (String) session.getAttribute("flashError");
            if (flashError != null) {
                req.setAttribute("errorMessage", flashError);
                session.removeAttribute("flashError");
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/challenge/browse.jsp").forward(req, resp);
    }

    private void showChallengeHistory(HttpServletRequest req, HttpServletResponse resp, int userId) throws ServletException, IOException {
        List<UserChallenge> history = challengeDAO.findUserChallengeHistory(userId);
        req.setAttribute("history", history);
        req.setAttribute("activeNav", "challenges");
        req.setAttribute("pageTitle", "Challenge History - PulseFit");
        req.setAttribute("pageHeading", "My Challenge Participation History");
        req.getRequestDispatcher("/WEB-INF/views/challenge/history.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Security validation failed (invalid CSRF token).");
            resp.sendRedirect(req.getContextPath() + "/challenges");
            return;
        }

        String action = req.getParameter("action");
        String challengeIdStr = req.getParameter("challengeId");

        try {
            int challengeId = Integer.parseInt(challengeIdStr);

            if ("join".equalsIgnoreCase(action)) {
                if (challengeDAO.isUserEnrolled(user.getId(), challengeId)) {
                    session.setAttribute("flashError", "You are already enrolled in this challenge.");
                } else {
                    challengeDAO.joinChallenge(user.getId(), challengeId);
                    logger.info("User {} joined challenge {}", user.getId(), challengeId);
                    session.setAttribute("flashSuccess", "Successfully enrolled in challenge! Your workout activities will now count towards this target.");
                }
            } else if ("leave".equalsIgnoreCase(action)) {
                challengeDAO.leaveChallenge(user.getId(), challengeId);
                logger.info("User {} left challenge {}", user.getId(), challengeId);
                session.setAttribute("flashSuccess", "You have left the challenge.");
            }

        } catch (Exception e) {
            logger.error("Error updating challenge participation", e);
            session.setAttribute("flashError", "Unable to update challenge participation.");
        }

        resp.sendRedirect(req.getContextPath() + "/challenges");
    }
}
