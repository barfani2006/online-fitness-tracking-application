package com.fitness.dao;

import com.fitness.model.FitnessGoal;
import com.fitness.model.GoalStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Access Object interface for FitnessGoal entities.
 */
public interface GoalDAO extends GenericDAO<FitnessGoal, Integer> {

    List<FitnessGoal> findByUserId(int userId);

    List<FitnessGoal> findActiveByUserId(int userId);

    boolean updateProgress(int goalId, int userId, BigDecimal newCurrentValue, GoalStatus newStatus);

    boolean updateStatus(int goalId, int userId, GoalStatus newStatus);

    boolean deleteByIdAndUserId(int goalId, int userId);

    int countCompletedGoalsAllUsers();

    int countActiveGoalsAllUsers();
}
