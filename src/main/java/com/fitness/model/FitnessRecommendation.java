package com.fitness.model;

import java.io.Serializable;

/**
 * Value object representing an automated, rule-based fitness suggestion.
 * Explicitly includes a non-medical advisory disclaimer.
 */
public class FitnessRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DISCLAIMER =
            "Informational Notice: This is an automated rule-based fitness recommendation based on logged workouts. " +
            "It is not medical advice. Consult a certified healthcare provider before altering your training or diet.";

    private String category;
    private String title;
    private String advice;
    private String priority; // LOW, MEDIUM, HIGH
    private String icon;

    public FitnessRecommendation() {
    }

    public FitnessRecommendation(String category, String title, String advice, String priority, String icon) {
        this.category = category;
        this.title = title;
        this.advice = advice;
        this.priority = priority;
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDisclaimer() {
        return DISCLAIMER;
    }
}
