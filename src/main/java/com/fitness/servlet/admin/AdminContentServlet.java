package com.fitness.servlet.admin;

import com.fitness.dao.ContentDAO;
import com.fitness.dao.impl.ContentDAOImpl;
import com.fitness.exception.ValidationException;
import com.fitness.model.ContentCategory;
import com.fitness.model.ContentStatus;
import com.fitness.model.FitnessContent;
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
import java.util.Optional;

/**
 * Controller for Administrator Fitness Content Moderation: create, review, approve, reject, update, and remove.
 */
@WebServlet(name = "AdminContentServlet", urlPatterns = {"/admin/content", "/admin/content/*"})
public class AdminContentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminContentServlet.class);
    private final ContentDAO contentDAO;

    public AdminContentServlet() {
        this(new ContentDAOImpl());
    }

    public AdminContentServlet(ContentDAO contentDAO) {
        this.contentDAO = contentDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equalsIgnoreCase(action)) {
            showNewForm(req, resp);
        } else if ("edit".equalsIgnoreCase(action)) {
            showEditForm(req, resp);
        } else {
            listContent(req, resp);
        }
    }

    private void listContent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String statusStr = req.getParameter("status");
        List<FitnessContent> contentList;
        if (statusStr != null && !statusStr.isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            ContentStatus status = ContentStatus.fromString(statusStr);
            contentList = contentDAO.findAllWithStatus(status);
        } else {
            contentList = contentDAO.findAll();
        }

        req.setAttribute("contentList", contentList);
        req.setAttribute("selectedStatus", statusStr != null ? statusStr : "ALL");
        req.setAttribute("pendingCount", contentDAO.countPendingContent());
        req.setAttribute("approvedCount", contentDAO.countApprovedContent());
        req.setAttribute("categories", ContentCategory.values());
        req.setAttribute("activeNav", "admin-content");
        req.setAttribute("pageTitle", "Content Moderation - Admin Console");
        req.setAttribute("pageHeading", "Fitness Content Review & Moderation");

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

        req.getRequestDispatcher("/WEB-INF/views/admin/content/manage.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", ContentCategory.values());
        req.setAttribute("activeNav", "admin-content");
        req.setAttribute("pageTitle", "Create Article - Admin Console");
        req.setAttribute("pageHeading", "Publish Educational Fitness Guide");
        CsrfUtil.generateToken(req.getSession(true));
        req.getRequestDispatcher("/WEB-INF/views/admin/content/form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/content");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Optional<FitnessContent> opt = contentDAO.findById(id);
            if (opt.isEmpty()) {
                req.getSession().setAttribute("flashError", "Content record not found.");
                resp.sendRedirect(req.getContextPath() + "/admin/content");
                return;
            }

            req.setAttribute("article", opt.get());
            req.setAttribute("categories", ContentCategory.values());
            req.setAttribute("isEdit", true);
            req.setAttribute("activeNav", "admin-content");
            req.setAttribute("pageTitle", "Edit Content - Admin Console");
            req.setAttribute("pageHeading", "Modify Educational Guide");
            CsrfUtil.generateToken(req.getSession(true));
            req.getRequestDispatcher("/WEB-INF/views/admin/content/form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/content");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (!CsrfUtil.isValidToken(req)) {
            session.setAttribute("flashError", "Security validation failed (invalid CSRF token).");
            resp.sendRedirect(req.getContextPath() + "/admin/content");
            return;
        }

        String action = req.getParameter("action");
        if ("approve".equalsIgnoreCase(action)) {
            handleApprove(req, resp);
        } else if ("reject".equalsIgnoreCase(action)) {
            handleReject(req, resp);
        } else if ("create".equalsIgnoreCase(action)) {
            handleCreate(req, resp);
        } else if ("update".equalsIgnoreCase(action)) {
            handleUpdate(req, resp);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDelete(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/content");
        }
    }

    private void handleApprove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            contentDAO.updateStatus(id, ContentStatus.APPROVED, null);
            logger.info("Admin approved fitness content id={}", id);
            req.getSession().setAttribute("flashSuccess", "Content approved and published to user community.");
        } catch (Exception e) {
            logger.error("Error approving content", e);
            req.getSession().setAttribute("flashError", "Failed to approve content.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/content");
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String reason = req.getParameter("rejectionReason");
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Content does not meet health guidelines or standards.";
            }
            contentDAO.updateStatus(id, ContentStatus.REJECTED, reason.trim());
            logger.info("Admin rejected fitness content id={}", id);
            req.getSession().setAttribute("flashSuccess", "Content rejected.");
        } catch (Exception e) {
            logger.error("Error rejecting content", e);
            req.getSession().setAttribute("flashError", "Failed to reject content.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/content");
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User admin = (User) req.getSession().getAttribute("currentUser");
            String title = req.getParameter("title");
            String categoryStr = req.getParameter("category");
            String content = req.getParameter("content");

            if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                throw new ValidationException("Title and content are required.");
            }

            FitnessContent fc = new FitnessContent();
            fc.setTitle(title.trim());
            fc.setContent(content.trim());
            fc.setCategory(ContentCategory.fromString(categoryStr));
            fc.setAuthorId(admin.getId());
            fc.setStatus(ContentStatus.APPROVED);

            contentDAO.save(fc);
            logger.info("Admin created and published guide: id={}", fc.getId());
            req.getSession().setAttribute("flashSuccess", "Article published successfully.");

        } catch (Exception e) {
            logger.error("Error creating content", e);
            req.getSession().setAttribute("flashError", "Failed to publish article: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/content");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Optional<FitnessContent> opt = contentDAO.findById(id);
            if (opt.isPresent()) {
                FitnessContent fc = opt.get();
                fc.setTitle(req.getParameter("title").trim());
                fc.setContent(req.getParameter("content").trim());
                fc.setCategory(ContentCategory.fromString(req.getParameter("category")));
                contentDAO.update(fc);
                logger.info("Admin updated content id={}", id);
                req.getSession().setAttribute("flashSuccess", "Article updated successfully.");
            }
        } catch (Exception e) {
            logger.error("Error updating content", e);
            req.getSession().setAttribute("flashError", "Failed to update article.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/content");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            contentDAO.deleteById(id);
            logger.info("Admin deleted content id={}", id);
            req.getSession().setAttribute("flashSuccess", "Content removed successfully.");
        } catch (Exception e) {
            logger.error("Error deleting content", e);
            req.getSession().setAttribute("flashError", "Failed to delete content.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/content");
    }
}
