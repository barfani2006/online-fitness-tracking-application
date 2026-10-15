package com.fitness.model;

public enum ChallengeMetric {
    WORKOUT_COUNT("Workouts", "workouts"),
    TOTAL_MINUTES("Active Minutes", "minutes"),
    CALORIES("Calories Burned", "kcal");

    private final String displayName;
    private final String unit;

    ChallengeMetric(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUnit() {
        return unit;
    }

    public static ChallengeMetric fromString(String val) {
        if (val == null) return WORKOUT_COUNT;
        try {
            return ChallengeMetric.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return WORKOUT_COUNT;
        }
    }
}
