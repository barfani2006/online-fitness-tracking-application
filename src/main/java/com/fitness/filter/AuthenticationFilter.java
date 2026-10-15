package com.fitness.filter;

import com.fitness.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter enforcing session-based authentication for user workflows.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/dashboard",
        "/workouts",
        "/workouts/*",
        "/goals",
        "/goals/*",
        "/challenges",
        "/challenges/*",
        "/profile",
        "/recommendations",
        "/content/submit"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            String redirectUrl = req.getRequestURI();
            String query = req.getQueryString();
            if (query != null) {
                redirectUrl += "?" + query;
            }
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("targetAfterLogin", redirectUrl);
            newSession.setAttribute("flashError", "Please sign in to access that page.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Prevent cached authenticated pages on back-button click
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }
}
