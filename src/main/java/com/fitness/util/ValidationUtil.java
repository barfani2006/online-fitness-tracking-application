package com.fitness.util;

import com.fitness.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Robust server-side validation and HTML sanitization utilities.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Minimum 8 characters, at least one digit and one letter
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,64}$"
    );

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static void validateRegistration(String fullName, String email, String password, String confirmPassword) {
        List<String> errors = new ArrayList<>();
        if (fullName == null || fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            errors.add("Full name must be between 2 and 100 characters.");
        }
        if (!isValidEmail(email)) {
            errors.add("Please provide a valid email address.");
        }
        if (!isValidPassword(password)) {
            errors.add("Password must be at least 8 characters long and contain both letters and numbers.");
        }
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.add("Passwords do not match.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void validateWorkout(String activityName, Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate) {
        List<String> errors = new ArrayList<>();
        if (activityName == null || activityName.trim().isEmpty() || activityName.trim().length() > 100) {
            errors.add("Activity name is required and cannot exceed 100 characters.");
        }
        if (durationMinutes == null || durationMinutes < 1 || durationMinutes > 720) {
            errors.add("Workout duration must be between 1 and 720 minutes.");
        }
        if (caloriesBurned == null || caloriesBurned < 0 || caloriesBurned > 10000) {
            errors.add("Calories burned must be between 0 and 10,000 kcal.");
        }
        if (workoutDate == null) {
            errors.add("Workout date is required.");
        } else if (workoutDate.isAfter(LocalDate.now())) {
            errors.add("Workout date cannot be in the future.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void validateGoal(String title, BigDecimal targetValue, LocalDate targetDate) {
        List<String> errors = new ArrayList<>();
        if (title == null || title.trim().isEmpty() || title.trim().length() > 150) {
            errors.add("Goal title is required and must not exceed 150 characters.");
        }
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Target value must be a positive number.");
        }
        if (targetDate == null) {
            errors.add("Target completion date is required.");
        } else if (targetDate.isBefore(LocalDate.now())) {
            errors.add("Target completion date must be today or in the future.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static LocalDate parseDateOrNull(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Escapes critical HTML characters to defend against Cross-Site Scripting (XSS).
     */
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
