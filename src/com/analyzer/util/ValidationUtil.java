package com.analyzer.util;

import java.util.regex.Pattern;

/**
 * VIVA EXPLANATION - OOP CONCEPT: EXCEPTION HANDLING & VALIDATION
 * This utility class provides robust business validation logic.
 * It uses custom Exception classes to report issues up the stack, which are caught by the GUI layer
 * to show user-friendly error alerts. This is standard enterprise architecture.
 */
public class ValidationUtil {

    // Regular Expression for basic email format validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Custom Exception for validation failures.
     */
    public static class ValidationException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Validates that a text string is not empty or null.
     */
    public static void validateNotEmpty(String fieldName, String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("❌ " + fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates that an ID/Roll number is alphanumeric and meets length.
     */
    public static void validateId(String id) throws ValidationException {
        validateNotEmpty("Student ID/Roll No", id);
        String trimmed = id.trim();
        if (trimmed.length() < 3) {
            throw new ValidationException("❌ Student ID must be at least 3 characters long.");
        }
        if (!trimmed.matches("[a-zA-Z0-9_-]+")) {
            throw new ValidationException("❌ Student ID must contain only alphanumeric characters, underscores, or hyphens.");
        }
    }

    /**
     * Validates that a string is a valid email format.
     */
    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty("Email Address", email);
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("❌ Invalid Email Address format (e.g., student@school.com).");
        }
    }

    /**
     * Validates academic score inputs (Marks, Assignment score, Attendance) to be numeric and in the range 0 to 100.
     */
    public static double validatePercentage(String fieldName, String valueStr) throws ValidationException {
        validateNotEmpty(fieldName, valueStr);
        try {
            double value = Double.parseDouble(valueStr.trim());
            if (value < 0.0 || value > 100.0) {
                throw new ValidationException("❌ " + fieldName + " must be between 0.0 and 100.0.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ValidationException("❌ " + fieldName + " must be a valid number (e.g., 85.5).");
        }
    }

    /**
     * Validates a double value directly to see if it lies between 0 and 100.
     */
    public static void validatePercentage(String fieldName, double value) throws ValidationException {
        if (value < 0.0 || value > 100.0) {
            throw new ValidationException("❌ " + fieldName + " must be between 0.0 and 100.0.");
        }
    }
}
