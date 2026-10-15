package com.fitness.util;

import com.fitness.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated unit tests verifying server-side input validation and HTML sanitization.
 */
class ValidationUtilTest {

    @Test
    @DisplayName("Email validation correctly accepts compliant addresses and rejects invalid formats")
    void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("alex.miller@gmail.com"));
        assertTrue(ValidationUtil.isValidEmail("john_doe@fitness.co.uk"));
        assertTrue(ValidationUtil.isValidEmail("user+tag@domain.org"));

        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtil.isValidEmail("user@.com.my"));
    }

    @Test
    @DisplayName("Password policy validates length (>=8) and combination of letters and digits")
    void testPasswordPolicyValidation() {
        assertTrue(ValidationUtil.isValidPassword("StrongPass123"));
        assertTrue(ValidationUtil.isValidPassword("secret99"));

        assertFalse(ValidationUtil.isValidPassword("short1")); // < 8 characters
        assertFalse(ValidationUtil.isValidPassword("allalphabeticletters")); // no digit
        assertFalse(ValidationUtil.isValidPassword("1234567890")); // no letter
        assertFalse(ValidationUtil.isValidPassword(null));
    }

    @Test
    @DisplayName("Registration validation passes on valid data and throws ValidationException on errors")
    void testValidateRegistration() {
        assertDoesNotThrow(() ->
                ValidationUtil.validateRegistration("Alex Miller", "alex@example.com", "Password123", "Password123"));

        // Mismatched passwords
        ValidationException ex1 = assertThrows(ValidationException.class, () ->
                ValidationUtil.validateRegistration("Alex Miller", "alex@example.com", "Password123", "Mismatch456"));
        assertTrue(ex1.getErrors().stream().anyMatch(e -> e.contains("match")));

        // Invalid email
        ValidationException ex2 = assertThrows(ValidationException.class, () ->
                ValidationUtil.validateRegistration("Alex Miller", "invalid-email", "Password123", "Password123"));
        assertTrue(ex2.getErrors().stream().anyMatch(e -> e.contains("email")));
    }

    @Test
    @DisplayName("Workout validation enforces positive duration, calorie bounds, and non-future dates")
    void testValidateWorkout() {
        LocalDate today = LocalDate.now();

        // Valid workout
        assertDoesNotThrow(() ->
                ValidationUtil.validateWorkout("Morning Run", 45, 380, today));

        // Future workout date is rejected
        assertThrows(ValidationException.class, () ->
                ValidationUtil.validateWorkout("Morning Run", 45, 380, today.plusDays(1)));

        // Non-positive duration rejected
        assertThrows(ValidationException.class, () ->
                ValidationUtil.validateWorkout("Morning Run", 0, 380, today));

        // Excessively large duration rejected (> 720 minutes / 12 hours)
        assertThrows(ValidationException.class, () ->
                ValidationUtil.validateWorkout("Morning Run", 800, 380, today));

        // Negative calories rejected
        assertThrows(ValidationException.class, () ->
                ValidationUtil.validateWorkout("Morning Run", 30, -50, today));
    }

    @Test
    @DisplayName("Goal validation enforces positive target and non-past target date")
    void testValidateGoal() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        assertDoesNotThrow(() ->
                ValidationUtil.validateGoal("Run 50km this month", new BigDecimal("50.0"), tomorrow));

        // Target value <= 0 rejected
        assertThrows(ValidationException.class, () ->
                ValidationUtil.validateGoal("Run 50km", BigDecimal.ZERO, tomorrow));

        // Target date in the past rejected
        assertThrows(ValidationException.class, () ->
                ValidationUtil.validateGoal("Run 50km", new BigDecimal("50.0"), LocalDate.now().minusDays(2)));
    }

    @Test
    @DisplayName("HTML sanitization replaces XSS injection vectors with safe entities")
    void testHtmlSanitization() {
        assertNull(ValidationUtil.escapeHtml(null));
        assertEquals("&lt;script&gt;alert(&quot;xss&quot;)&lt;/script&gt;",
                ValidationUtil.escapeHtml("<script>alert(\"xss\")</script>"));
        assertEquals("Tom &amp; Jerry", ValidationUtil.escapeHtml("Tom & Jerry"));
        assertEquals("User&#x27;s Profile", ValidationUtil.escapeHtml("User's Profile"));
    }
}
