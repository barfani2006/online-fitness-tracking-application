package com.fitness.model;

public enum UserStatus {
    ACTIVE,
    INACTIVE;

    public static UserStatus fromString(String val) {
        if (val == null) return ACTIVE;
        try {
            return UserStatus.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
