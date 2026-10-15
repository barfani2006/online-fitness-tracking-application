package com.fitness.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Synchronizer Token Pattern helper for Cross-Site Request Forgery (CSRF) mitigation.
 */
public final class CsrfUtil {

    public static final String CSRF_SESSION_KEY = "CSRF_TOKEN";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    private static final SecureRandom secureRandom = new SecureRandom();

    private CsrfUtil() {
    }

    /**
     * Generates a cryptographically random token and caches it in the active session.
     */
    public static String generateToken(HttpSession session) {
        if (session == null) return null;
        String existingToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (existingToken != null && !existingToken.isEmpty()) {
            return existingToken;
        }
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_SESSION_KEY, token);
        return token;
    }

    /**
     * Validates that the submitted CSRF parameter matches the session token.
     */
    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (sessionToken == null || sessionToken.isEmpty()) return false;

        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.isEmpty()) {
            requestToken = request.getHeader("X-CSRF-TOKEN");
        }
        return sessionToken.equals(requestToken);
    }
}
