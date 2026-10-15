package com.fitness.servlet;

import com.fitness.dao.UserDAO;
import com.fitness.dao.impl.UserDAOImpl;
import com.fitness.model.User;
import com.fitness.model.UserStatus;
import com.fitness.util.CsrfUtil;
import com.fitness.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller handling user authentication, session initiation, and redirect routing.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UserDAO userDAO;

    public LoginServlet() {
        this(new UserDAOImpl());
    }

    public LoginServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            }
            return;
        }

        // Transfer flash messages if any
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

        CsrfUtil.generateToken(session);
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        if (!CsrfUtil.isValidToken(req)) {
            req.setAttribute("errorMessage", "Security validation failed (invalid CSRF token). Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("errorMessage", "Email and password are required.");
            req.setAttribute("formEmail", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim());

        if (userOpt.isEmpty() || !PasswordUtil.checkPassword(password, userOpt.get().getPasswordHash())) {
            logger.warn("Failed login attempt for email: {}", email);
            com.fitness.util.AsyncAuditLogger.getInstance().log(null, "LOGIN_FAILURE", "User", null,
                    "Failed login attempt for email: " + email, req.getRemoteAddr());
            req.setAttribute("errorMessage", "Invalid email address or password.");
            req.setAttribute("formEmail", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        User user = userOpt.get();

        if (user.getStatus() != UserStatus.ACTIVE) {
            logger.warn("Inactive user attempted login: id={}, email={}", user.getId(), user.getEmail());
            com.fitness.util.AsyncAuditLogger.getInstance().log(user.getId(), "LOGIN_FAILURE", "User", user.getId(),
                    "Inactive account attempted login: " + user.getEmail(), req.getRemoteAddr());
            req.setAttribute("errorMessage", "Your account has been deactivated. Please contact an administrator.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        // Prevent session fixation by invalidating and regenerating session
        session.invalidate();
        HttpSession newSession = req.getSession(true);
        newSession.setAttribute("currentUser", user);
        CsrfUtil.generateToken(newSession);

        logger.info("User logged in successfully: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole());
        com.fitness.util.AsyncAuditLogger.getInstance().log(user.getId(), "LOGIN_SUCCESS", "User", user.getId(),
                "User authenticated successfully as " + user.getRole(), req.getRemoteAddr());

        if (user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
