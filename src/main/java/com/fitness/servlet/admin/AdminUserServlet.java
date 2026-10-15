package com.fitness.servlet.admin;

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
import java.util.List;
import java.util.Optional;

/**
 * Controller for Administrator User Management: listing, searching, editing, role assignment, status toggling, and deletion.
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users", "/admin/users/*"})
public class AdminUserServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserServlet.class);
    private final UserDAO userDAO;

    public AdminUserServlet() {
        this(new UserDAOImpl());
    }

    public AdminUserServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equalsIgnoreCase(action)) {
            showNewUserForm(req, resp);
        } else if ("edit".equalsIgnoreCase(action)) {
            showEditUserForm(req, resp);
        } else {
            listUsers(req, resp);
        }
    }

    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String roleStr = req.getParameter("role");
        String statusStr = req.getParameter("status");
        String pageStr = req.getParameter("page");

        UserRole role = (roleStr != null && !roleStr.isEmpty()) ? UserRole.fromString(roleStr) : null;
        UserStatus status = (statusStr != null && !statusStr.isEmpty()) ? UserStatus.fromString(statusStr) : null;

        int page = 1;
        int pageSize = 10;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {
            }
        }
        int offset = (page - 1) * pageSize;

        int totalCount = userDAO.countUsers(query, role, status);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        List<User> users = userDAO.searchUsers(query, role, status, offset, pageSize);

        req.setAttribute("users", users);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("query", query);
        req.setAttribute("selectedRole", roleStr);
        req.setAttribute("selectedStatus", statusStr);
        req.setAttribute("roles", UserRole.values());
        req.setAttribute("statuses", UserStatus.values());
        req.setAttribute("activeNav", "admin-users");
        req.setAttribute("pageTitle", "User Management - Admin Console");
        req.setAttribute("pageHeading", "User Accounts Administration");

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

        req.getRequestDispatcher("/WEB-INF/views/admin/users/list.jsp").forward(req, resp);
    }

    private void showNewUserForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("roles", UserRole.values());
        req.setAttribute("statuses", UserStatus.values());
        req.setAttribute("fitnessLevels", FitnessLevel.values());
        req.setAttribute("genders", Gender.values());
        req.setAttribute("activeNav", "admin-users");
        req.setAttribute("pageTitle", "Create User - Admin Console");
        req.setAttribute("pageHeading", "Create New User Account");
        CsrfUtil.generateToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/admin/users/form.jsp").forward(req, resp);
    }

    private void showEditUserForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        try {
            int userId = Integer.parseInt(idStr);
            Optional<User> userOpt = userDAO.findById(userId);
            if (userOpt.isEmpty()) {
                req.getSession().setAttribute("flashError", "User not found.");
                resp.sendRedirect(req.getContextPath() + "/admin/users");
                return;
            }

            req.setAttribute("targetUser", userOpt.get());
            req.setAttribute("isEdit", true);
            req.setAttribute("roles", UserRole.values());
            req.setAttribute("statuses", UserStatus.values());
            req.setAttribute("fitnessLevels", FitnessLevel.values());
            req.setAttribute("genders", Gender.values());
            req.setAttribute("activeNav", "admin-users");
            req.setAttribute("pageTitle", "Edit User - Admin Console");
            req.setAttribute("pageHeading", "Modify User Account Details");
            CsrfUtil.generateToken(req.getSession(true));
            req.getRequestDispatcher("/WEB-INF/views/admin/users/form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Security validation failed (invalid CSRF token).");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        String action = req.getParameter("action");
        if ("create".equalsIgnoreCase(action)) {
            handleCreateUser(req, resp);
        } else if ("update".equalsIgnoreCase(action)) {
            handleUpdateUser(req, resp);
        } else if ("toggle_status".equalsIgnoreCase(action)) {
            handleToggleStatus(req, resp);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDeleteUser(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void handleCreateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String roleStr = req.getParameter("role");
            String statusStr = req.getParameter("status");

            if (fullName == null || fullName.trim().length() < 2) {
                throw new ValidationException("Full name must be at least 2 characters.");
            }
            if (!ValidationUtil.isValidEmail(email)) {
                throw new ValidationException("Invalid email format.");
            }
            if (!ValidationUtil.isValidPassword(password)) {
                throw new ValidationException("Password must be at least 8 chars with letters & digits.");
            }
            if (userDAO.emailExists(email)) {
                throw new ValidationException("A user with this email already exists.");
            }

            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRole(UserRole.fromString(roleStr));
            user.setStatus(UserStatus.fromString(statusStr));

            userDAO.save(user);
            logger.info("Admin created new user account: id={}, email={}", user.getId(), user.getEmail());
            User admin = (User) req.getSession().getAttribute("currentUser");
            com.fitness.util.AsyncAuditLogger.getInstance().log(admin != null ? admin.getId() : null,
                    "USER_CREATED", "User", user.getId(),
                    "Admin created user " + user.getEmail() + " (" + user.getRole() + ")", req.getRemoteAddr());
            req.getSession().setAttribute("flashSuccess", "User account created successfully.");

        } catch (ValidationException ve) {
            req.getSession().setAttribute("flashError", ve.getMessage());
        } catch (Exception e) {
            logger.error("Error creating user as admin", e);
            req.getSession().setAttribute("flashError", "Failed to create user: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void handleUpdateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            Optional<User> existingOpt = userDAO.findById(userId);
            if (existingOpt.isEmpty()) {
                req.getSession().setAttribute("flashError", "User not found.");
                resp.sendRedirect(req.getContextPath() + "/admin/users");
                return;
            }

            User user = existingOpt.get();
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String roleStr = req.getParameter("role");
            String statusStr = req.getParameter("status");

            user.setFullName(fullName.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setRole(UserRole.fromString(roleStr));
            user.setStatus(UserStatus.fromString(statusStr));

            userDAO.update(user);
            logger.info("Admin updated user details: id={}", user.getId());
            User admin = (User) req.getSession().getAttribute("currentUser");
            com.fitness.util.AsyncAuditLogger.getInstance().log(admin != null ? admin.getId() : null,
                    "USER_UPDATED", "User", user.getId(),
                    "Admin updated user " + user.getEmail() + " (Role: " + user.getRole() + ", Status: " + user.getStatus() + ")", req.getRemoteAddr());
            req.getSession().setAttribute("flashSuccess", "User updated successfully.");

        } catch (Exception e) {
            logger.error("Error updating user", e);
            req.getSession().setAttribute("flashError", "Failed to update user.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void handleToggleStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            Optional<User> existingOpt = userDAO.findById(userId);
            if (existingOpt.isPresent()) {
                User user = existingOpt.get();
                UserStatus newStatus = (user.getStatus() == UserStatus.ACTIVE) ? UserStatus.INACTIVE : UserStatus.ACTIVE;
                userDAO.updateStatus(userId, newStatus);
                logger.info("Admin toggled user status: id={}, newStatus={}", userId, newStatus);
                User admin = (User) req.getSession().getAttribute("currentUser");
                com.fitness.util.AsyncAuditLogger.getInstance().log(admin != null ? admin.getId() : null,
                        "USER_STATUS_CHANGED", "User", userId,
                        "Admin changed user status for " + user.getEmail() + " to " + newStatus, req.getRemoteAddr());
                req.getSession().setAttribute("flashSuccess", "User status changed to " + newStatus);
            }
        } catch (Exception e) {
            logger.error("Error toggling user status", e);
            req.getSession().setAttribute("flashError", "Failed to toggle user status.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void handleDeleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            User admin = (User) req.getSession().getAttribute("currentUser");
            if (admin != null && admin.getId() == userId) {
                req.getSession().setAttribute("flashError", "You cannot delete your own administrative account.");
                resp.sendRedirect(req.getContextPath() + "/admin/users");
                return;
            }

            userDAO.deleteById(userId);
            logger.info("Admin deleted user: id={}", userId);
            com.fitness.util.AsyncAuditLogger.getInstance().log(admin != null ? admin.getId() : null,
                    "USER_DELETED", "User", userId,
                    "Admin deleted user account ID: " + userId, req.getRemoteAddr());
            req.getSession().setAttribute("flashSuccess", "User account deleted permanently.");

        } catch (Exception e) {
            logger.error("Error deleting user", e);
            req.getSession().setAttribute("flashError", "Failed to delete user: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
