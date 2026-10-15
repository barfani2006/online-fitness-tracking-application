package com.fitness.dao;

import com.fitness.model.ContentCategory;
import com.fitness.model.ContentStatus;
import com.fitness.model.FitnessContent;

import java.util.List;

/**
 * Data Access Object interface for Fitness Content and moderation workflows.
 */
public interface ContentDAO extends GenericDAO<FitnessContent, Integer> {

    List<FitnessContent> findApprovedContent();

    List<FitnessContent> findApprovedByCategory(ContentCategory category);

    List<FitnessContent> findPendingContent();

    List<FitnessContent> findAllWithStatus(ContentStatus status);

    boolean updateStatus(int contentId, ContentStatus status, String rejectionReason);

    int countPendingContent();

    int countApprovedContent();
}
