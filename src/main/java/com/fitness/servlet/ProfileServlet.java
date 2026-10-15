package com.fitness.servlet;

import com.fitness.dao.UserDAO;
import com.fitness.dao.impl.UserDAOImpl;
import com.fitness.exception.ValidationException;
import com.fitness.model.FitnessLevel;
import com.fitness.model.Gender;
import com.fitness.model.User;
import com.fitness.util.CsrfUtil;
import com.fitness.util.PasswordUtil;
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
import java.util.Optional;

/**
 * Controller managing user profile details, biometric indicators (BMI), and secure password changes.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile", "/profile/*"})
public class ProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);
    private final UserDAO userDAO;

    public ProfileServlet() {
        this(new UserDAOImpl());
    }

    public ProfileServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (sessionUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Always reload fresh user entity from database
        Optional<User> freshUserOpt = userDAO.findById(sessionUser.getId());
        User user = freshUserOpt.orElse(sessionUser);
        session.setAttribute("currentUser", user);

        req.setAttribute("user", user);
        req.setAttribute("bmi", user.calculateBMI());
        req.setAttribute("fitnessLevels", FitnessLevel.values());
        req.setAttribute("genders", Gender.values());
        req.setAttribute("activeNav", "profile");
        req.setAttribute("pageTitle", "My Profile - PulseFit");
        req.setAttribute("pageHeading", "Profile & Health Settings");

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

        CsrfUtil.generateToken(session);
        req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, resp);
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
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String action = req.getParameter("action");
        if ("update_profile".equalsIgnoreCase(action)) {
            handleUpdateProfile(req, resp, user);
        } else if ("change_password".equalsIgnoreCase(action)) {
            handleChangePassword(req, resp, user);
        } else {
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

    private void handleUpdateProfile(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            String fullName = req.getParameter("fullName");
            String ageStr = req.getParameter("age");
            String genderStr = req.getParameter("gender");
            String weightStr = req.getParameter("weightKg");
            String heightStr = req.getParameter("heightCm");
            String fitnessLevelStr = req.getParameter("fitnessLevel");

            if (fullName == null || fullName.trim().length() < 2) {
                throw new ValidationException("Full name must be at least 2 characters.");
            }

            user.setFullName(fullName.trim());

            if (ageStr != null && !ageStr.trim().isEmpty()) {
                int age = Integer.parseInt(ageStr.trim());
                if (age >= 10 && age <= 120) user.setAge(age);
            }
            if (genderStr != null && !genderStr.trim().isEmpty()) {
                user.setGender(Gender.fromString(genderStr));
            }
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                user.setWeightKg(new BigDecimal(weightStr.trim()));
            }
            if (heightStr != null && !heightStr.trim().isEmpty()) {
                user.setHeightCm(new BigDecimal(heightStr.trim()));
            }
            if (fitnessLevelStr != null && !fitnessLevelStr.trim().isEmpty()) {
                user.setFitnessLevel(FitnessLevel.fromString(fitnessLevelStr));
            }

            userDAO.updateProfile(user);
            req.getSession().setAttribute("currentUser", user);
            req.getSession().setAttribute("flashSuccess", "Profile details updated successfully.");
            logger.info("User {} updated profile details", user.getId());

        } catch (ValidationException ve) {
            req.getSession().setAttribute("flashError", ve.getMessage());
        } catch (Exception e) {
            logger.error("Error updating profile", e);
            req.getSession().setAttribute("flashError", "Failed to update profile: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmNewPassword = req.getParameter("confirmNewPassword");

            Optional<User> dbUserOpt = userDAO.findById(user.getId());
            if (dbUserOpt.isEmpty() || !PasswordUtil.checkPassword(currentPassword, dbUserOpt.get().getPasswordHash())) {
                req.getSession().setAttribute("flashError", "Current password does not match.");
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }

            if (!ValidationUtil.isValidPassword(newPassword)) {
                req.getSession().setAttribute("flashError", "New password must be at least 8 characters long and contain both letters and digits.");
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }

            if (confirmNewPassword == null || !confirmNewPassword.equals(newPassword)) {
                req.getSession().setAttribute("flashError", "New passwords do not match.");
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            userDAO.updatePassword(user.getId(), newHash);
            logger.info("User {} changed password successfully", user.getId());

            req.getSession().setAttribute("flashSuccess", "Password changed successfully.");

        } catch (Exception e) {
            logger.error("Error changing password", e);
            req.getSession().setAttribute("flashError", "Failed to change password: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
