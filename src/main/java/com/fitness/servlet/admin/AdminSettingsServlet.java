package com.fitness.servlet.admin;

import com.fitness.dao.SettingDAO;
import com.fitness.dao.impl.SettingDAOImpl;
import com.fitness.model.AppSetting;
import com.fitness.model.User;
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
 * Controller for managing system configuration settings.
 */
@WebServlet(name = "AdminSettingsServlet", urlPatterns = {"/admin/settings"})
public class AdminSettingsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminSettingsServlet.class);
    private final SettingDAO settingDAO;

    public AdminSettingsServlet() {
        this(new SettingDAOImpl());
    }

    public AdminSettingsServlet(SettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AppSetting> settings = settingDAO.findAll();
        req.setAttribute("settings", settings);
        req.setAttribute("activeNav", "admin-settings");
        req.setAttribute("pageTitle", "System Settings - Admin Console");
        req.setAttribute("pageHeading", "Application Configuration Parameters");

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

        CsrfUtil.generateToken(session);
        req.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Security validation failed (invalid CSRF token).");
            resp.sendRedirect(req.getContextPath() + "/admin/settings");
            return;
        }

        try {
            List<AppSetting> settings = settingDAO.findAll();
            for (AppSetting s : settings) {
                String newVal = req.getParameter(s.getKey());
                if (newVal != null) {
                    settingDAO.updateSetting(s.getKey(), newVal.trim());
                }
            }
            logger.info("Admin updated application configuration settings");
            User admin = (User) session.getAttribute("currentUser");
            com.fitness.util.AsyncAuditLogger.getInstance().log(admin != null ? admin.getId() : null,
                    "SETTINGS_UPDATED", "AppSetting", null, "Admin updated system configuration settings", req.getRemoteAddr());
            session.setAttribute("flashSuccess", "System configuration settings updated successfully.");

        } catch (Exception e) {
            logger.error("Error updating system settings", e);
            session.setAttribute("flashError", "Failed to update settings: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/settings");
    }
}
