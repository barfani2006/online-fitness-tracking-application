package com.fitness.servlet;

import com.fitness.dao.GoalDAO;
import com.fitness.dao.impl.GoalDAOImpl;
import com.fitness.exception.ValidationException;
import com.fitness.model.FitnessGoal;
import com.fitness.model.GoalStatus;
import com.fitness.model.GoalType;
import com.fitness.model.User;
import com.fitness.util.CsrfUtil;
import com.fitness.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller managing personal fitness goal setting, progress updates, milestone tracking, and deletion.
 */
@WebServlet(name = "GoalServlet", urlPatterns = {"/goals", "/goals/*"})
public class GoalServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(GoalServlet.class);
    private final GoalDAO goalDAO;

    public GoalServlet() {
        this(new GoalDAOImpl());
    }

    public GoalServlet(GoalDAO goalDAO) {
        this.goalDAO = goalDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
                showNewForm(req, resp);
                break;
            case "edit":
                showEditForm(req, resp, user.getId());
                break;
            default:
                listGoals(req, resp, user.getId());
                break;
        }
    }

    private void listGoals(HttpServletRequest req, HttpServletResponse resp, int userId) throws ServletException, IOException {
        List<FitnessGoal> goals = goalDAO.findByUserId(userId);
        req.setAttribute("goals", goals);
        req.setAttribute("activeNav", "goals");
        req.setAttribute("pageTitle", "Fitness Goals - PulseFit");
        req.setAttribute("pageHeading", "Personal Fitness Goals & Milestones");

        HttpSession session = req.getSession(false);
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

        req.getRequestDispatcher("/WEB-INF/views/goal/list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("goalTypes", GoalType.values());
        req.setAttribute("todayDate", LocalDate.now().toString());
        req.setAttribute("activeNav", "goals");
        req.setAttribute("pageTitle", "Create Goal - PulseFit");
        req.setAttribute("pageHeading", "Set New Fitness Goal");
        CsrfUtil.generateToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/goal/form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp, int userId) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/goals");
            return;
        }

        try {
            int goalId = Integer.parseInt(idStr);
            Optional<FitnessGoal> goalOpt = goalDAO.findById(goalId);
            if (goalOpt.isEmpty() || goalOpt.get().getUserId() != userId) {
                req.getSession().setAttribute("flashError", "Goal record not found or access denied.");
                resp.sendRedirect(req.getContextPath() + "/goals");
                return;
            }

            req.setAttribute("goal", goalOpt.get());
            req.setAttribute("goalTypes", GoalType.values());
            req.setAttribute("isEdit", true);
            req.setAttribute("activeNav", "goals");
            req.setAttribute("pageTitle", "Edit Goal - PulseFit");
            req.setAttribute("pageHeading", "Update Fitness Goal");
            CsrfUtil.generateToken(req.getSession(true));
            req.getRequestDispatcher("/WEB-INF/views/goal/form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/goals");
        }
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
            resp.sendRedirect(req.getContextPath() + "/goals");
            return;
        }

        String action = req.getParameter("action");
        if ("create".equalsIgnoreCase(action)) {
            handleCreateGoal(req, resp, user);
        } else if ("update".equalsIgnoreCase(action)) {
            handleUpdateGoal(req, resp, user);
        } else if ("update_progress".equalsIgnoreCase(action)) {
            handleUpdateProgress(req, resp, user);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDeleteGoal(req, resp, user);
        } else {
            resp.sendRedirect(req.getContextPath() + "/goals");
        }
    }

    private void handleCreateGoal(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        try {
            String title = req.getParameter("title");
            String goalTypeStr = req.getParameter("goalType");
            String targetValueStr = req.getParameter("targetValue");
            String unit = req.getParameter("unit");
            String startDateStr = req.getParameter("startDate");
            String targetDateStr = req.getParameter("targetDate");

            GoalType goalType = GoalType.fromString(goalTypeStr);
            BigDecimal targetValue = new BigDecimal(targetValueStr.trim());
            LocalDate startDate = ValidationUtil.parseDateOrNull(startDateStr);
            LocalDate targetDate = ValidationUtil.parseDateOrNull(targetDateStr);

            ValidationUtil.validateGoal(title, targetValue, targetDate);

            if (unit == null || unit.trim().isEmpty()) {
                unit = goalType.getDefaultUnit();
            }

            FitnessGoal goal = new FitnessGoal();
            goal.setUserId(user.getId());
            goal.setTitle(title.trim());
            goal.setGoalType(goalType);
            goal.setTargetValue(targetValue);
            goal.setCurrentValue(BigDecimal.ZERO);
            goal.setUnit(unit.trim());
            goal.setStartDate(startDate != null ? startDate : LocalDate.now());
            goal.setTargetDate(targetDate);
            goal.setStatus(GoalStatus.ACTIVE);

            goalDAO.save(goal);
            logger.info("Goal created: id={}, user={}, title={}", goal.getId(), user.getId(), goal.getTitle());

            req.getSession().setAttribute("flashSuccess", "Fitness goal created successfully!");
            resp.sendRedirect(req.getContextPath() + "/goals");

        } catch (ValidationException ve) {
            req.setAttribute("errorMessage", ve.getMessage());
            showNewForm(req, resp);
        } catch (Exception e) {
            logger.error("Error creating goal", e);
            req.setAttribute("errorMessage", "Failed to create goal: " + e.getMessage());
            showNewForm(req, resp);
        }
    }

    private void handleUpdateGoal(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            int goalId = Integer.parseInt(req.getParameter("id"));
            Optional<FitnessGoal> existing = goalDAO.findById(goalId);
            if (existing.isEmpty() || existing.get().getUserId() != user.getId()) {
                req.getSession().setAttribute("flashError", "Goal not found or access denied.");
                resp.sendRedirect(req.getContextPath() + "/goals");
                return;
            }

            String title = req.getParameter("title");
            String targetValueStr = req.getParameter("targetValue");
            String targetDateStr = req.getParameter("targetDate");
            String statusStr = req.getParameter("status");

            FitnessGoal g = existing.get();
            g.setTitle(title.trim());
            g.setTargetValue(new BigDecimal(targetValueStr.trim()));
            g.setTargetDate(ValidationUtil.parseDateOrNull(targetDateStr));
            if (statusStr != null) {
                g.setStatus(GoalStatus.fromString(statusStr));
            }

            goalDAO.update(g);
            logger.info("Goal updated: id={}, user={}", g.getId(), user.getId());

            req.getSession().setAttribute("flashSuccess", "Goal updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/goals");

        } catch (Exception e) {
            logger.error("Error updating goal", e);
            req.getSession().setAttribute("flashError", "Failed to update goal: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/goals");
        }
    }

    private void handleUpdateProgress(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            int goalId = Integer.parseInt(req.getParameter("id"));
            BigDecimal newCurrent = new BigDecimal(req.getParameter("currentValue").trim());

            Optional<FitnessGoal> existing = goalDAO.findById(goalId);
            if (existing.isPresent() && existing.get().getUserId() == user.getId()) {
                FitnessGoal g = existing.get();
                GoalStatus newStatus = (newCurrent.compareTo(g.getTargetValue()) >= 0) ? GoalStatus.COMPLETED : GoalStatus.ACTIVE;
                goalDAO.updateProgress(goalId, user.getId(), newCurrent, newStatus);
                req.getSession().setAttribute("flashSuccess", "Goal progress updated!");
            }
        } catch (Exception e) {
            logger.error("Error updating progress value", e);
            req.getSession().setAttribute("flashError", "Failed to update progress value.");
        }
        resp.sendRedirect(req.getContextPath() + "/goals");
    }

    private void handleDeleteGoal(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            int goalId = Integer.parseInt(req.getParameter("id"));
            boolean deleted = goalDAO.deleteByIdAndUserId(goalId, user.getId());
            if (deleted) {
                req.getSession().setAttribute("flashSuccess", "Goal deleted successfully.");
            } else {
                req.getSession().setAttribute("flashError", "Goal not found or access denied.");
            }
        } catch (Exception e) {
            logger.error("Error deleting goal", e);
            req.getSession().setAttribute("flashError", "Failed to delete goal.");
        }
        resp.sendRedirect(req.getContextPath() + "/goals");
    }
}
