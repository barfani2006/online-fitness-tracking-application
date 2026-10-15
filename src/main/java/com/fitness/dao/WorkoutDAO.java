package com.fitness.dao;

import com.fitness.model.Workout;
import com.fitness.model.WorkoutCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Workout records and category metadata.
 */
public interface WorkoutDAO extends GenericDAO<Workout, Integer> {

    List<Workout> findRecentByUserId(int userId, int limit);

    List<Workout> findByUserId(int userId);

    List<Workout> searchWorkouts(int userId, String keyword, Integer categoryId,
                                  LocalDate startDate, LocalDate endDate,
                                  String sortBy, String sortOrder, int offset, int limit);

    int countWorkouts(int userId, String keyword, Integer categoryId, LocalDate startDate, LocalDate endDate);

    Map<String, Object> getUserWorkoutStats(int userId);

    List<Map<String, Object>> getWeeklyWorkoutTrends(int userId, int weeks);

    List<Map<String, Object>> getIntensityBreakdown(int userId);

    List<Map<String, Object>> getCategoryDistribution(int userId);

    List<WorkoutCategory> findAllCategories();

    int countTotalWorkoutsAllUsers();

    long sumTotalCaloriesAllUsers();

    long sumTotalMinutesAllUsers();
}
