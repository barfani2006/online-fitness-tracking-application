package com.fitness.servlet.admin;

import com.fitness.dao.AuditLogDAO;
import com.fitness.dao.impl.AuditLogDAOImpl;
import com.fitness.model.AuditLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Administrator System Activity and Audit Monitoring.
 * Allows administrators to monitor system events, security operations, and user changes.
 */
@WebServlet(name = "AdminActivityServlet", urlPatterns = {"/admin/activity"})
public class AdminActivityServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminActivityServlet.class);
    private final AuditLogDAO auditLogDAO;

    public AdminActivityServlet() {
        this(new AuditLogDAOImpl());
    }

    public AdminActivityServlet(AuditLogDAO auditLogDAO) {
        this.auditLogDAO = auditLogDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String actionFilter = req.getParameter("action");
            int limit = 50;

            List<AuditLog> logs;
            if (actionFilter != null && !actionFilter.trim().isEmpty()) {
                logs = auditLogDAO.findByAction(actionFilter.trim(), limit);
            } else {
                logs = auditLogDAO.findRecent(limit);
            }

            int totalLogs = auditLogDAO.countTotalLogs();

            req.setAttribute("logs", logs);
            req.setAttribute("totalCount", totalLogs);
            req.setAttribute("selectedAction", actionFilter);
            req.setAttribute("activeNav", "admin-activity");
            req.setAttribute("pageTitle", "Activity Audit Logs - Admin Console");
            req.setAttribute("pageHeading", "Administrative & Security Audit Logs");

            req.getRequestDispatcher("/WEB-INF/views/admin/activity.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error loading activity audit logs", e);
            req.setAttribute("errorMessage", "Failed to retrieve activity logs: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        }
    }
}
