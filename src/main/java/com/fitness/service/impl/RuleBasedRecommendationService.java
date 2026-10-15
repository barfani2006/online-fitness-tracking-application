package com.fitness.service.impl;

import com.fitness.model.FitnessGoal;
import com.fitness.model.FitnessLevel;
import com.fitness.model.FitnessRecommendation;
import com.fitness.model.User;
import com.fitness.model.Workout;
import com.fitness.model.WorkoutIntensity;
import com.fitness.service.RecommendationService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic rule-based fitness recommendation engine.
 * Computes recommendations from workout frequency, intensity balance, recovery gaps, and goal alignment.
 */
public class RuleBasedRecommendationService implements RecommendationService {

    @Override
    public List<FitnessRecommendation> generateRecommendations(User user, List<Workout> recentWorkouts, List<FitnessGoal> activeGoals) {
        List<FitnessRecommendation> list = new ArrayList<>();

        if (recentWorkouts == null || recentWorkouts.isEmpty()) {
            list.add(new FitnessRecommendation(
                    "Getting Started",
                    "Begin with Moderate Aerobic Conditioning",
                    "You haven't logged any workouts recently. Aim for a 20-30 minute brisk walk, cycle, or gentle jog 3 times this week to build baseline cardiovascular stamina.",
                    "HIGH",
                    "play-circle"
            ));
            list.add(new FitnessRecommendation(
                    "Goal Setting",
                    "Establish a Weekly Consistency Target",
                    "Setting an initial goal of 3 workouts per week significantly increases training adherence. Visit the Goals tab to create your first milestone.",
                    "MEDIUM",
                    "target"
            ));
            return list;
        }

        // Rule 1: Rest & Recovery Analysis
        Workout latest = recentWorkouts.get(0);
        long daysSinceLast = ChronoUnit.DAYS.between(latest.getWorkoutDate(), LocalDate.now());

        if (daysSinceLast >= 4) {
            list.add(new FitnessRecommendation(
                    "Consistency Alert",
                    "Resume Training Momentum",
                    "It has been " + daysSinceLast + " days since your last logged workout (" + latest.getActivityName() + "). Re-engage with light mobility or a moderate 25-minute session today.",
                    "HIGH",
                    "clock"
            ));
        } else if (daysSinceLast == 0 && recentWorkouts.size() >= 3) {
            // Check consecutive heavy days
            int highIntensityCount = 0;
            for (int i = 0; i < Math.min(3, recentWorkouts.size()); i++) {
                WorkoutIntensity inten = recentWorkouts.get(i).getIntensity();
                if (inten == WorkoutIntensity.HIGH || inten == WorkoutIntensity.EXTREME) {
                    highIntensityCount++;
                }
            }
            if (highIntensityCount >= 2) {
                list.add(new FitnessRecommendation(
                        "Recovery & Injury Prevention",
                        "Schedule an Active Recovery Day",
                        "You logged multiple high-intensity sessions recently. Allow your central nervous system and muscles 24-48 hours of low-intensity walking, stretching, or hydration.",
                        "MEDIUM",
                        "heart"
                ));
            }
        }

        // Rule 2: Workout Volume & Intensity Balance
        int totalMinutesPastWeek = 0;
        int extremeCount = 0;
        int lowCount = 0;
        for (Workout w : recentWorkouts) {
            if (ChronoUnit.DAYS.between(w.getWorkoutDate(), LocalDate.now()) <= 7) {
                totalMinutesPastWeek += w.getDurationMinutes();
                if (w.getIntensity() == WorkoutIntensity.EXTREME) extremeCount++;
                if (w.getIntensity() == WorkoutIntensity.LOW) lowCount++;
            }
        }

        if (totalMinutesPastWeek < 90) {
            list.add(new FitnessRecommendation(
                    "Aerobic Conditioning",
                    "Aim for 150 Weekly Exercise Minutes",
                    "Your logged activity in the last 7 days is " + totalMinutesPastWeek + " minutes. Target at least 150 minutes of moderate aerobic activity for cardiovascular wellness.",
                    "MEDIUM",
                    "activity"
            ));
        } else if (totalMinutesPastWeek > 300) {
            list.add(new FitnessRecommendation(
                    "High Performance",
                    "Outstanding Volume - Monitor Nutrition & Sleep",
                    "You achieved " + totalMinutesPastWeek + " active minutes this past week! Prioritize protein intake (1.6-2.0g/kg bodyweight) and 8 hours of sleep to support cellular repair.",
                    "LOW",
                    "award"
            ));
        }

        // Rule 3: Goal-Specific Recommendations
        if (activeGoals != null && !activeGoals.isEmpty()) {
            for (FitnessGoal goal : activeGoals) {
                if (goal.getProgressPercentage() < 30 && ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate()) < 10) {
                    list.add(new FitnessRecommendation(
                            "Goal Progress",
                            "Accelerate Progress for: " + goal.getTitle(),
                            "Target deadline is in " + ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate()) +
                                    " days and you are currently at " + goal.getProgressPercentage() + "% completion. Consider adding an extra short session.",
                            "HIGH",
                            "check-circle"
                    ));
                    break;
                }
            }
        } else {
            list.add(new FitnessRecommendation(
                    "Goal Planning",
                    "Set a Quantifiable Fitness Target",
                    "Users with defined workout or calorie goals achieve 40% higher training consistency. Navigate to Goals to define a target.",
                    "LOW",
                    "flag"
            ));
        }

        // Rule 4: User Profile Personalization
        if (user != null && user.getFitnessLevel() == FitnessLevel.BEGINNER) {
            list.add(new FitnessRecommendation(
                    "Technique Foundation",
                    "Focus on Form Before Intensity",
                    "As a beginner, prioritize controlled movement patterns and steady breathing over heavy resistance or maximum speed to prevent early burnout.",
                    "LOW",
                    "compass"
            ));
        }

        return list;
    }
}
