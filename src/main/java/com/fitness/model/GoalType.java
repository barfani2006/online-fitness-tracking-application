package com.fitness.model;

public enum GoalType {
    WORKOUT_COUNT("Total Workouts", "workouts"),
    TOTAL_DURATION("Total Duration", "minutes"),
    CALORIES_BURNED("Calories Burned", "kcal"),
    WEIGHT_TARGET("Target Bodyweight", "kg");

    private final String displayName;
    private final String defaultUnit;

    GoalType(String displayName, String defaultUnit) {
        this.displayName = displayName;
        this.defaultUnit = defaultUnit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public static GoalType fromString(String val) {
        if (val == null) return WORKOUT_COUNT;
        try {
            return GoalType.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return WORKOUT_COUNT;
        }
    }
}
