package com.fitness.exception;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when server-side form or business validation fails.
 */
public class ValidationException extends AppException {

    private final List<String> errors;

    public ValidationException(String message) {
        super(message, 400);
        this.errors = Collections.singletonList(message);
    }

    public ValidationException(List<String> errors) {
        super(errors != null && !errors.isEmpty() ? errors.get(0) : "Validation failed", 400);
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public List<String> getErrors() {
        return errors;
    }
}
