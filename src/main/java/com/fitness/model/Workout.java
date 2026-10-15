package com.fitness.model;

import java.time.LocalDate;

/**
 * Domain model representing a single workout activity logged by a user.
 */
public class Workout extends BaseEntity {

    private Integer userId;
    private Integer categoryId;
    private String categoryName;
    private String activityName;
    private LocalDate workoutDate = LocalDate.now();
    private Integer durationMinutes;
    private WorkoutIntensity intensity = WorkoutIntensity.MEDIUM;
    private Integer caloriesBurned;
    private String notes;

    public Workout() {
    }

    public Workout(Integer id, Integer userId, Integer categoryId, String activityName,
                   LocalDate workoutDate, Integer durationMinutes, WorkoutIntensity intensity,
                   Integer caloriesBurned, String notes) {
        super(id);
        this.userId = userId;
        this.categoryId = categoryId;
        this.activityName = activityName;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity != null ? intensity : WorkoutIntensity.MEDIUM;
        this.caloriesBurned = caloriesBurned;
        this.notes = notes;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public WorkoutIntensity getIntensity() {
        return intensity;
    }

    public void setIntensity(WorkoutIntensity intensity) {
        this.intensity = intensity;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
