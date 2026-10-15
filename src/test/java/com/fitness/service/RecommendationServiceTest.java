package com.fitness.service;

import com.fitness.model.FitnessGoal;
import com.fitness.model.FitnessLevel;
import com.fitness.model.FitnessRecommendation;
import com.fitness.model.GoalType;
import com.fitness.model.User;
import com.fitness.model.Workout;
import com.fitness.model.WorkoutIntensity;
import com.fitness.service.impl.RuleBasedRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated unit tests verifying rule-based recommendation engine logic
 * and non-medical advisory compliance.
 */
class RecommendationServiceTest {

    private RecommendationService recommendationService;
    private User testUser;

    @BeforeEach
    void setUp() {
        recommendationService = new RuleBasedRecommendationService();

        testUser = new User();
        testUser.setId(1);
        testUser.setFullName("Test Athlete");
        testUser.setEmail("athlete@example.com");
        testUser.setFitnessLevel(FitnessLevel.INTERMEDIATE);
    }

    @Test
    @DisplayName("Empty workout history yields getting started guidance and non-medical disclaimer")
    void testEmptyHistoryGeneratesOnboardingRecommendations() {
        List<FitnessRecommendation> recs = recommendationService.generateRecommendations(
                testUser, Collections.emptyList(), Collections.emptyList());

        assertNotNull(recs);
        assertFalse(recs.isEmpty());
        assertTrue(recs.stream().anyMatch(r -> r.getTitle().contains("Getting Started") || r.getTitle().contains("Moderate")));

        // Verify non-medical disclaimer
        assertNotNull(FitnessRecommendation.DISCLAIMER);
        assertTrue(FitnessRecommendation.DISCLAIMER.contains("not medical advice"));
    }

    @Test
    @DisplayName("User with gap of 5 days receives consistency alert")
    void testInactivityGeneratesConsistencyAlert() {
        List<Workout> workouts = new ArrayList<>();
        Workout oldWorkout = new Workout();
        oldWorkout.setUserId(1);
        oldWorkout.setActivityName("Jogging");
        oldWorkout.setDurationMinutes(30);
        oldWorkout.setCaloriesBurned(250);
        oldWorkout.setWorkoutDate(LocalDate.now().minusDays(5));
        oldWorkout.setIntensity(WorkoutIntensity.MEDIUM);
        workouts.add(oldWorkout);

        List<FitnessRecommendation> recs = recommendationService.generateRecommendations(
                testUser, workouts, Collections.emptyList());

        assertNotNull(recs);
        assertTrue(recs.stream().anyMatch(r -> r.getCategory().contains("Consistency Alert") || r.getAdvice().contains("5 days")));
    }

    @Test
    @DisplayName("Consecutive extreme workouts trigger recovery and injury prevention suggestion")
    void testExcessiveHighIntensityTriggersRecoveryAdvice() {
        List<Workout> workouts = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Workout w = new Workout();
            w.setUserId(1);
            w.setActivityName("HIIT Sprint Session " + i);
            w.setDurationMinutes(45);
            w.setCaloriesBurned(450);
            w.setWorkoutDate(LocalDate.now()); // same day or consecutive
            w.setIntensity(WorkoutIntensity.EXTREME);
            workouts.add(w);
        }

        List<FitnessRecommendation> recs = recommendationService.generateRecommendations(
                testUser, workouts, Collections.emptyList());

        assertNotNull(recs);
        assertTrue(recs.stream().anyMatch(r -> r.getCategory().contains("Recovery")));
    }

    @Test
    @DisplayName("Active goals generate motivational target-alignment guidance")
    void testActiveGoalsTriggerAlignmentRecommendation() {
        List<Workout> workouts = new ArrayList<>();
        Workout w = new Workout();
        w.setUserId(1);
        w.setActivityName("Gym");
        w.setDurationMinutes(40);
        w.setCaloriesBurned(300);
        w.setWorkoutDate(LocalDate.now());
        w.setIntensity(WorkoutIntensity.MEDIUM);
        workouts.add(w);

        List<FitnessGoal> goals = new ArrayList<>();
        FitnessGoal goal = new FitnessGoal();
        goal.setTitle("Lose 5kg");
        goal.setGoalType(GoalType.WEIGHT_TARGET);
        goal.setTargetValue(new BigDecimal("10.0"));
        goal.setCurrentValue(new BigDecimal("2.0")); // 20% progress
        goal.setTargetDate(LocalDate.now().plusDays(5)); // 5 days left (< 10 days)
        goals.add(goal);

        List<FitnessRecommendation> recs = recommendationService.generateRecommendations(
                testUser, workouts, goals);

        assertNotNull(recs);
        assertTrue(recs.stream().anyMatch(r -> r.getCategory().contains("Goal") || r.getTitle().contains("Goal")));
    }
}
