package com.fitness.model;

public enum GoalStatus {
    ACTIVE,
    COMPLETED,
    EXPIRED;

    public static GoalStatus fromString(String val) {
        if (val == null) return ACTIVE;
        try {
            return GoalStatus.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
