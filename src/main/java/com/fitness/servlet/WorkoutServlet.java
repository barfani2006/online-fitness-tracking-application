package com.fitness.servlet;

import com.fitness.dao.ChallengeDAO;
import com.fitness.dao.GoalDAO;
import com.fitness.dao.WorkoutDAO;
import com.fitness.dao.impl.ChallengeDAOImpl;
import com.fitness.dao.impl.GoalDAOImpl;
import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.exception.ValidationException;
import com.fitness.model.FitnessGoal;
import com.fitness.model.GoalStatus;
import com.fitness.model.GoalType;
import com.fitness.model.User;
import com.fitness.model.UserChallenge;
import com.fitness.model.Workout;
import com.fitness.model.WorkoutCategory;
import com.fitness.model.WorkoutIntensity;
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
 * Controller managing workout logging, listing, editing, searching, and deleting.
 */
@WebServlet(name = "WorkoutServlet", urlPatterns = {"/workouts", "/workouts/*"})
public class WorkoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutServlet.class);
    private final WorkoutDAO workoutDAO;
    private final GoalDAO goalDAO;
    private final ChallengeDAO challengeDAO;

    public WorkoutServlet() {
        this(new WorkoutDAOImpl(), new GoalDAOImpl(), new ChallengeDAOImpl());
    }

    public WorkoutServlet(WorkoutDAO workoutDAO, GoalDAO goalDAO, ChallengeDAO challengeDAO) {
        this.workoutDAO = workoutDAO;
        this.goalDAO = goalDAO;
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
                listWorkouts(req, resp, user.getId());
                break;
        }
    }

    private void listWorkouts(HttpServletRequest req, HttpServletResponse resp, int userId) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String categoryIdStr = req.getParameter("categoryId");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");
        String sortBy = req.getParameter("sortBy");
        String sortOrder = req.getParameter("sortOrder");
        String pageStr = req.getParameter("page");

        Integer categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty()) ? Integer.parseInt(categoryIdStr) : null;
        LocalDate startDate = ValidationUtil.parseDateOrNull(startDateStr);
        LocalDate endDate = ValidationUtil.parseDateOrNull(endDateStr);

        int page = 1;
        int pageSize = 10;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {
            }
        }
        int offset = (page - 1) * pageSize;

        int totalCount = workoutDAO.countWorkouts(userId, keyword, categoryId, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        List<Workout> workouts = workoutDAO.searchWorkouts(userId, keyword, categoryId, startDate, endDate, sortBy, sortOrder, offset, pageSize);
        List<WorkoutCategory> categories = workoutDAO.findAllCategories();

        req.setAttribute("workouts", workouts);
        req.setAttribute("categories", categories);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("startDate", startDateStr);
        req.setAttribute("endDate", endDateStr);
        req.setAttribute("sortBy", sortBy);
        req.setAttribute("sortOrder", sortOrder);
        req.setAttribute("activeNav", "workouts");
        req.setAttribute("pageTitle", "My Workouts - PulseFit");
        req.setAttribute("pageHeading", "Workout Management & Log History");

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

        req.getRequestDispatcher("/WEB-INF/views/workout/list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", workoutDAO.findAllCategories());
        req.setAttribute("intensities", WorkoutIntensity.values());
        req.setAttribute("todayDate", LocalDate.now().toString());
        req.setAttribute("activeNav", "workouts");
        req.setAttribute("pageTitle", "Log Workout - PulseFit");
        req.setAttribute("pageHeading", "Record New Workout Activity");
        CsrfUtil.generateToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/workout/form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp, int userId) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/workouts");
            return;
        }

        try {
            int workoutId = Integer.parseInt(idStr);
            Optional<Workout> workoutOpt = workoutDAO.findById(workoutId);
            if (workoutOpt.isEmpty() || workoutOpt.get().getUserId() != userId) {
                req.getSession().setAttribute("flashError", "Workout record not found or access denied.");
                resp.sendRedirect(req.getContextPath() + "/workouts");
                return;
            }

            req.setAttribute("workout", workoutOpt.get());
            req.setAttribute("categories", workoutDAO.findAllCategories());
            req.setAttribute("intensities", WorkoutIntensity.values());
            req.setAttribute("isEdit", true);
            req.setAttribute("activeNav", "workouts");
            req.setAttribute("pageTitle", "Edit Workout - PulseFit");
            req.setAttribute("pageHeading", "Update Workout Record");
            CsrfUtil.generateToken(req.getSession(true));
            req.getRequestDispatcher("/WEB-INF/views/workout/form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/workouts");
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
            resp.sendRedirect(req.getContextPath() + "/workouts");
            return;
        }

        String action = req.getParameter("action");
        if ("create".equalsIgnoreCase(action)) {
            handleCreateWorkout(req, resp, user);
        } else if ("update".equalsIgnoreCase(action)) {
            handleUpdateWorkout(req, resp, user);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDeleteWorkout(req, resp, user);
        } else {
            resp.sendRedirect(req.getContextPath() + "/workouts");
        }
    }

    private void handleCreateWorkout(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        try {
            String activityName = req.getParameter("activityName");
            String categoryIdStr = req.getParameter("categoryId");
            String workoutDateStr = req.getParameter("workoutDate");
            String durationMinutesStr = req.getParameter("durationMinutes");
            String intensityStr = req.getParameter("intensity");
            String caloriesBurnedStr = req.getParameter("caloriesBurned");
            String notes = req.getParameter("notes");

            Integer categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty()) ? Integer.parseInt(categoryIdStr) : null;
            LocalDate workoutDate = ValidationUtil.parseDateOrNull(workoutDateStr);
            Integer duration = (durationMinutesStr != null && !durationMinutesStr.isEmpty()) ? Integer.parseInt(durationMinutesStr) : null;
            WorkoutIntensity intensity = WorkoutIntensity.fromString(intensityStr);

            Integer calories = null;
            if (caloriesBurnedStr != null && !caloriesBurnedStr.trim().isEmpty()) {
                calories = Integer.parseInt(caloriesBurnedStr.trim());
            } else if (duration != null) {
                // Approximate estimation: duration * 8 * intensityMultiplier
                calories = (int) Math.round(duration * 7.5 * intensity.getCalorieFactor());
            }

            ValidationUtil.validateWorkout(activityName, duration, calories, workoutDate);

            Workout w = new Workout();
            w.setUserId(user.getId());
            w.setCategoryId(categoryId);
            w.setActivityName(activityName.trim());
            w.setWorkoutDate(workoutDate);
            w.setDurationMinutes(duration);
            w.setIntensity(intensity);
            w.setCaloriesBurned(calories);
            w.setNotes(notes);

            workoutDAO.save(w);
            logger.info("Workout logged: id={}, user={}, activity={}", w.getId(), user.getId(), w.getActivityName());

            // Automated goal update trigger
            syncActiveGoals(user.getId(), duration, calories);

            // Automated challenge progress trigger
            syncActiveChallenges(user.getId(), duration, calories);

            req.getSession().setAttribute("flashSuccess", "Workout record successfully logged!");
            resp.sendRedirect(req.getContextPath() + "/workouts");

        } catch (ValidationException ve) {
            req.setAttribute("errorMessage", ve.getMessage());
            showNewForm(req, resp);
        } catch (Exception e) {
            logger.error("Error creating workout", e);
            req.setAttribute("errorMessage", "Failed to save workout: " + e.getMessage());
            showNewForm(req, resp);
        }
    }

    private void handleUpdateWorkout(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        try {
            int workoutId = Integer.parseInt(req.getParameter("id"));
            Optional<Workout> existing = workoutDAO.findById(workoutId);
            if (existing.isEmpty() || existing.get().getUserId() != user.getId()) {
                req.getSession().setAttribute("flashError", "Unauthorized or invalid workout record.");
                resp.sendRedirect(req.getContextPath() + "/workouts");
                return;
            }

            String activityName = req.getParameter("activityName");
            String categoryIdStr = req.getParameter("categoryId");
            String workoutDateStr = req.getParameter("workoutDate");
            String durationMinutesStr = req.getParameter("durationMinutes");
            String intensityStr = req.getParameter("intensity");
            String caloriesBurnedStr = req.getParameter("caloriesBurned");
            String notes = req.getParameter("notes");

            Integer categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty()) ? Integer.parseInt(categoryIdStr) : null;
            LocalDate workoutDate = ValidationUtil.parseDateOrNull(workoutDateStr);
            Integer duration = Integer.parseInt(durationMinutesStr);
            Integer calories = Integer.parseInt(caloriesBurnedStr);
            WorkoutIntensity intensity = WorkoutIntensity.fromString(intensityStr);

            ValidationUtil.validateWorkout(activityName, duration, calories, workoutDate);

            Workout w = existing.get();
            w.setCategoryId(categoryId);
            w.setActivityName(activityName.trim());
            w.setWorkoutDate(workoutDate);
            w.setDurationMinutes(duration);
            w.setIntensity(intensity);
            w.setCaloriesBurned(calories);
            w.setNotes(notes);

            workoutDAO.update(w);
            logger.info("Workout updated: id={}, user={}", w.getId(), user.getId());

            req.getSession().setAttribute("flashSuccess", "Workout record updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/workouts");

        } catch (Exception e) {
            logger.error("Error updating workout", e);
            req.getSession().setAttribute("flashError", "Failed to update workout: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/workouts");
        }
    }

    private void handleDeleteWorkout(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            int workoutId = Integer.parseInt(req.getParameter("id"));
            boolean deleted = ((WorkoutDAOImpl) workoutDAO).deleteByIdAndUserId(workoutId, user.getId());
            if (deleted) {
                req.getSession().setAttribute("flashSuccess", "Workout deleted successfully.");
            } else {
                req.getSession().setAttribute("flashError", "Workout not found or access denied.");
            }
        } catch (Exception e) {
            logger.error("Error deleting workout", e);
            req.getSession().setAttribute("flashError", "Failed to delete workout.");
        }
        resp.sendRedirect(req.getContextPath() + "/workouts");
    }

    private void syncActiveGoals(int userId, int durationMinutes, int caloriesBurned) {
        try {
            List<FitnessGoal> goals = goalDAO.findActiveByUserId(userId);
            for (FitnessGoal g : goals) {
                BigDecimal increment = BigDecimal.ZERO;
                if (g.getGoalType() == GoalType.WORKOUT_COUNT) {
                    increment = BigDecimal.ONE;
                } else if (g.getGoalType() == GoalType.TOTAL_DURATION) {
                    increment = new BigDecimal(durationMinutes);
                } else if (g.getGoalType() == GoalType.CALORIES_BURNED) {
                    increment = new BigDecimal(caloriesBurned);
                }

                if (increment.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal newTotal = g.getCurrentValue().add(increment);
                    GoalStatus newStatus = (newTotal.compareTo(g.getTargetValue()) >= 0) ? GoalStatus.COMPLETED : GoalStatus.ACTIVE;
                    goalDAO.updateProgress(g.getId(), userId, newTotal, newStatus);
                }
            }
        } catch (Exception e) {
            logger.warn("Goal synchronization warning: {}", e.getMessage());
        }
    }

    private void syncActiveChallenges(int userId, int durationMinutes, int caloriesBurned) {
        try {
            List<UserChallenge> challenges = challengeDAO.findUserActiveChallenges(userId);
            for (UserChallenge uc : challenges) {
                int addProgress = 0;
                switch (uc.getTargetMetric()) {
                    case WORKOUT_COUNT:
                        addProgress = 1;
                        break;
                    case TOTAL_MINUTES:
                        addProgress = durationMinutes;
                        break;
                    case CALORIES:
                        addProgress = caloriesBurned;
                        break;
                }
                if (addProgress > 0) {
                    challengeDAO.updateChallengeProgress(userId, uc.getChallengeId(), addProgress);
                }
            }
        } catch (Exception e) {
            logger.warn("Challenge synchronization warning: {}", e.getMessage());
        }
    }
}
