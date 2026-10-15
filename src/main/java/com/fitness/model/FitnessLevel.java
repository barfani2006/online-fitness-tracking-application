package com.fitness.model;

public enum FitnessLevel {
    BEGINNER("Beginner", "Light activity, learning proper forms"),
    INTERMEDIATE("Intermediate", "Consistent exercise 3-4 days/week"),
    ADVANCED("Advanced", "Dedicated athlete, high intensity 5+ days/week");

    private final String displayName;
    private final String description;

    FitnessLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static FitnessLevel fromString(String val) {
        if (val == null) return BEGINNER;
        try {
            return FitnessLevel.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return BEGINNER;
        }
    }
}
