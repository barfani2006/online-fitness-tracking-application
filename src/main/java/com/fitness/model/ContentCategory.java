package com.fitness.model;

public enum ContentCategory {
    NUTRITION("Nutrition & Diet"),
    WORKOUT("Workout Technique"),
    RECOVERY("Recovery & Sleep"),
    MOTIVATION("Mindset & Motivation"),
    GENERAL("General Health");

    private final String displayName;

    ContentCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ContentCategory fromString(String val) {
        if (val == null) return GENERAL;
        try {
            return ContentCategory.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GENERAL;
        }
    }
}
