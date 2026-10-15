package com.fitness.model;

public enum UserRole {
    USER,
    ADMIN;

    public static UserRole fromString(String val) {
        if (val == null) return USER;
        try {
            return UserRole.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
