package com.fitness.model;

public enum WorkoutIntensity {
    LOW("Low", 0.8, "#10b981"),
    MEDIUM("Medium", 1.0, "#3b82f6"),
    HIGH("High", 1.25, "#f59e0b"),
    EXTREME("Extreme", 1.5, "#ef4444");

    private final String displayName;
    private final double calorieFactor;
    private final String colorCode;

    WorkoutIntensity(String displayName, double calorieFactor, String colorCode) {
        this.displayName = displayName;
        this.calorieFactor = calorieFactor;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCalorieFactor() {
        return calorieFactor;
    }

    public String getColorCode() {
        return colorCode;
    }

    public static WorkoutIntensity fromString(String val) {
        if (val == null) return MEDIUM;
        try {
            return WorkoutIntensity.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
