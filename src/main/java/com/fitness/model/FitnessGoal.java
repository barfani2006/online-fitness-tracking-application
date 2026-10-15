package com.fitness.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Domain model representing a user's quantifiable fitness goal.
 */
public class FitnessGoal extends BaseEntity {

    private Integer userId;
    private String title;
    private GoalType goalType = GoalType.WORKOUT_COUNT;
    private BigDecimal targetValue;
    private BigDecimal currentValue = BigDecimal.ZERO;
    private String unit;
    private LocalDate startDate = LocalDate.now();
    private LocalDate targetDate;
    private GoalStatus status = GoalStatus.ACTIVE;

    public FitnessGoal() {
    }

    public FitnessGoal(Integer id, Integer userId, String title, GoalType goalType,
                       BigDecimal targetValue, BigDecimal currentValue, String unit,
                       LocalDate startDate, LocalDate targetDate, GoalStatus status) {
        super(id);
        this.userId = userId;
        this.title = title;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.currentValue = currentValue != null ? currentValue : BigDecimal.ZERO;
        this.unit = unit;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.status = status != null ? status : GoalStatus.ACTIVE;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    /**
     * Calculates the completion percentage capped at 100%.
     */
    public int getProgressPercentage() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        if (currentValue == null || currentValue.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal pct = currentValue.divide(targetValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        return Math.min(100, Math.max(0, pct.intValue()));
    }

    public boolean isCompleted() {
        return this.status == GoalStatus.COMPLETED || getProgressPercentage() >= 100;
    }
}
