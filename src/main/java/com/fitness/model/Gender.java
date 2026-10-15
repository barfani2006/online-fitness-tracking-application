package com.fitness.model;

public enum Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY;

    public static Gender fromString(String val) {
        if (val == null) return PREFER_NOT_TO_SAY;
        try {
            return Gender.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PREFER_NOT_TO_SAY;
        }
    }
}
