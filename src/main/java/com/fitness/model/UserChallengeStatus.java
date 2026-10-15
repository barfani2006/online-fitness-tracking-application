package com.fitness.model;

public enum UserChallengeStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED;

    public static UserChallengeStatus fromString(String val) {
        if (val == null) return IN_PROGRESS;
        try {
            return UserChallengeStatus.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return IN_PROGRESS;
        }
    }
}
