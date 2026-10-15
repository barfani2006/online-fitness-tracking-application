package com.fitness.model;

import java.math.BigDecimal;

/**
 * Category of workout exercise (e.g. Running, Strength Training, Swimming).
 */
public class WorkoutCategory extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal metValue = new BigDecimal("4.00");
    private String iconName = "dumbbell";

    public WorkoutCategory() {
    }

    public WorkoutCategory(Integer id, String name, String description, BigDecimal metValue, String iconName) {
        super(id);
        this.name = name;
        this.description = description;
        this.metValue = metValue;
        this.iconName = iconName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMetValue() {
        return metValue;
    }

    public void setMetValue(BigDecimal metValue) {
        this.metValue = metValue;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
