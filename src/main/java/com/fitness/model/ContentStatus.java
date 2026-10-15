package com.fitness.model;

public enum ContentStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static ContentStatus fromString(String val) {
        if (val == null) return PENDING;
        try {
            return ContentStatus.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
