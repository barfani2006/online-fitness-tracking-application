package com.fitness.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object encapsulating calculated workout metrics, frequency, and trend statistics.
 */
public class UserProgressSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalWorkouts;
    private long totalDurationMinutes;
    private long totalCaloriesBurned;
    private double averageDurationMinutes;
    private double consistencyScore; // 0 - 100

    private int thisWeekCount;
    private long thisWeekMinutes;
    private long thisWeekCalories;

    private int lastWeekCount;
    private long lastWeekMinutes;
    private long lastWeekCalories;

    private double durationWeeklyGrowthPct;
    private double calorieWeeklyGrowthPct;

    private List<Map<String, Object>> dailyTrends;
    private List<Map<String, Object>> intensityDistribution;
    private List<Map<String, Object>> categoryDistribution;

    public UserProgressSummary() {
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public long getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(long totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public long getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(long totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public double getAverageDurationMinutes() {
        return averageDurationMinutes;
    }

    public void setAverageDurationMinutes(double averageDurationMinutes) {
        this.averageDurationMinutes = averageDurationMinutes;
    }

    public double getConsistencyScore() {
        return consistencyScore;
    }

    public void setConsistencyScore(double consistencyScore) {
        this.consistencyScore = consistencyScore;
    }

    public int getThisWeekCount() {
        return thisWeekCount;
    }

    public void setThisWeekCount(int thisWeekCount) {
        this.thisWeekCount = thisWeekCount;
    }

    public long getThisWeekMinutes() {
        return thisWeekMinutes;
    }

    public void setThisWeekMinutes(long thisWeekMinutes) {
        this.thisWeekMinutes = thisWeekMinutes;
    }

    public long getThisWeekCalories() {
        return thisWeekCalories;
    }

    public void setThisWeekCalories(long thisWeekCalories) {
        this.thisWeekCalories = thisWeekCalories;
    }

    public int getLastWeekCount() {
        return lastWeekCount;
    }

    public void setLastWeekCount(int lastWeekCount) {
        this.lastWeekCount = lastWeekCount;
    }

    public long getLastWeekMinutes() {
        return lastWeekMinutes;
    }

    public void setLastWeekMinutes(long lastWeekMinutes) {
        this.lastWeekMinutes = lastWeekMinutes;
    }

    public long getLastWeekCalories() {
        return lastWeekCalories;
    }

    public void setLastWeekCalories(long lastWeekCalories) {
        this.lastWeekCalories = lastWeekCalories;
    }

    public double getDurationWeeklyGrowthPct() {
        return durationWeeklyGrowthPct;
    }

    public void setDurationWeeklyGrowthPct(double durationWeeklyGrowthPct) {
        this.durationWeeklyGrowthPct = durationWeeklyGrowthPct;
    }

    public double getCalorieWeeklyGrowthPct() {
        return calorieWeeklyGrowthPct;
    }

    public void setCalorieWeeklyGrowthPct(double calorieWeeklyGrowthPct) {
        this.calorieWeeklyGrowthPct = calorieWeeklyGrowthPct;
    }

    public List<Map<String, Object>> getDailyTrends() {
        return dailyTrends;
    }

    public void setDailyTrends(List<Map<String, Object>> dailyTrends) {
        this.dailyTrends = dailyTrends;
    }

    public List<Map<String, Object>> getIntensityDistribution() {
        return intensityDistribution;
    }

    public void setIntensityDistribution(List<Map<String, Object>> intensityDistribution) {
        this.intensityDistribution = intensityDistribution;
    }

    public List<Map<String, Object>> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(List<Map<String, Object>> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
}
