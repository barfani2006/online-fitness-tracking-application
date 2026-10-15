package com.fitness.servlet;

import com.fitness.dao.UserDAO;
import com.fitness.dao.impl.UserDAOImpl;
import com.fitness.exception.ValidationException;
import com.fitness.model.FitnessLevel;
import com.fitness.model.Gender;
import com.fitness.model.User;
import com.fitness.model.UserRole;
import com.fitness.model.UserStatus;
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

/**
 * Controller handling user registration workflow.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private final UserDAO userDAO;

    public RegisterServlet() {
        this(new UserDAOImpl());
    }

    public RegisterServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        CsrfUtil.generateToken(session);
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        if (!CsrfUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Security validation failed (invalid CSRF token). Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String ageStr = req.getParameter("age");
        String genderStr = req.getParameter("gender");
        String fitnessLevelStr = req.getParameter("fitnessLevel");
        String weightStr = req.getParameter("weightKg");
        String heightStr = req.getParameter("heightCm");

        try {
            ValidationUtil.validateRegistration(fullName, email, password, confirmPassword);

            if (userDAO.emailExists(email)) {
                throw new ValidationException("An account with this email address already exists.");
            }

            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRole(UserRole.USER);
            user.setStatus(UserStatus.ACTIVE);

            if (ageStr != null && !ageStr.trim().isEmpty()) {
                int age = Integer.parseInt(ageStr.trim());
                if (age >= 10 && age <= 120) user.setAge(age);
            }
            if (genderStr != null && !genderStr.trim().isEmpty()) {
                user.setGender(Gender.fromString(genderStr));
            }
            if (fitnessLevelStr != null && !fitnessLevelStr.trim().isEmpty()) {
                user.setFitnessLevel(FitnessLevel.fromString(fitnessLevelStr));
            }
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                user.setWeightKg(new BigDecimal(weightStr.trim()));
            }
            if (heightStr != null && !heightStr.trim().isEmpty()) {
                user.setHeightCm(new BigDecimal(heightStr.trim()));
            }

            userDAO.save(user);
            logger.info("New user registered successfully: id={}, email={}", user.getId(), user.getEmail());
            com.fitness.util.AsyncAuditLogger.getInstance().log(user.getId(), "USER_REGISTER", "User", user.getId(),
                    "User registered self-service account: " + user.getEmail(), req.getRemoteAddr());

            session.setAttribute("flashSuccess", "Registration successful! You can now log in with your credentials.");
            resp.sendRedirect(req.getContextPath() + "/login");

        } catch (ValidationException ve) {
            req.setAttribute("errorMessage", ve.getMessage());
            req.setAttribute("formFullName", fullName);
            req.setAttribute("formEmail", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Registration failed unexpectedly", e);
            req.setAttribute("errorMessage", "An error occurred during registration. Please check your inputs and try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }
}
