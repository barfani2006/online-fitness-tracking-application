package com.fitness.service.impl;

import com.fitness.dao.WorkoutDAO;
import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.model.UserProgressSummary;
import com.fitness.service.ProgressService;

import java.util.List;
import java.util.Map;

/**
 * Service implementation for progress analytics and calculations.
 */
public class ProgressServiceImpl implements ProgressService {

    private final WorkoutDAO workoutDAO;

    public ProgressServiceImpl() {
        this(new WorkoutDAOImpl());
    }

    public ProgressServiceImpl(WorkoutDAO workoutDAO) {
        this.workoutDAO = workoutDAO;
    }

    @Override
    public UserProgressSummary calculateUserProgress(int userId) {
        UserProgressSummary summary = new UserProgressSummary();

        Map<String, Object> baseStats = workoutDAO.getUserWorkoutStats(userId);
        int totalCount = (int) baseStats.getOrDefault("totalCount", 0);
        long totalMinutes = (long) baseStats.getOrDefault("totalMinutes", 0L);
        long totalCalories = (long) baseStats.getOrDefault("totalCalories", 0L);
        long avgDuration = (long) baseStats.getOrDefault("avgDuration", 0L);
        int thisWeekCount = (int) baseStats.getOrDefault("thisWeekCount", 0);
        long thisWeekMinutes = (long) baseStats.getOrDefault("thisWeekMinutes", 0L);
        long thisWeekCalories = (long) baseStats.getOrDefault("thisWeekCalories", 0L);

        summary.setTotalWorkouts(totalCount);
        summary.setTotalDurationMinutes(totalMinutes);
        summary.setTotalCaloriesBurned(totalCalories);
        summary.setAverageDurationMinutes(avgDuration);
        summary.setThisWeekCount(thisWeekCount);
        summary.setThisWeekMinutes(thisWeekMinutes);
        summary.setThisWeekCalories(thisWeekCalories);

        // Derive consistency score based on recent workout frequency (target: 3 to 4 days/week)
        double score = Math.min(100.0, Math.round((thisWeekCount / 4.0) * 100.0));
        summary.setConsistencyScore(score);

        // Fetch granular trends & distributions
        List<Map<String, Object>> dailyTrends = workoutDAO.getWeeklyWorkoutTrends(userId, 4);
        List<Map<String, Object>> intensityDist = workoutDAO.getIntensityBreakdown(userId);
        List<Map<String, Object>> categoryDist = workoutDAO.getCategoryDistribution(userId);

        summary.setDailyTrends(dailyTrends);
        summary.setIntensityDistribution(intensityDist);
        summary.setCategoryDistribution(categoryDist);

        return summary;
    }
}
