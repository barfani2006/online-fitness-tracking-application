package com.fitness.service;

import com.fitness.model.UserProgressSummary;

/**
 * Service contract for calculating user workout metrics, weekly comparisons, and frequency statistics.
 */
public interface ProgressService {

    UserProgressSummary calculateUserProgress(int userId);
}
