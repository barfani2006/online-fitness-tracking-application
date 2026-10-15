package com.fitness.service;

import com.fitness.model.FitnessGoal;
import com.fitness.model.FitnessRecommendation;
import com.fitness.model.User;
import com.fitness.model.Workout;

import java.util.List;

/**
 * Service contract for generating personalized algorithmic fitness guidance.
 */
public interface RecommendationService {

    List<FitnessRecommendation> generateRecommendations(User user, List<Workout> recentWorkouts, List<FitnessGoal> activeGoals);
}
