package com.fitness.dao;

import com.fitness.model.FitnessChallenge;
import com.fitness.model.UserChallenge;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Fitness Challenges and User Participation.
 */
public interface ChallengeDAO extends GenericDAO<FitnessChallenge, Integer> {

    List<FitnessChallenge> findAllWithUserStatus(int userId);

    List<UserChallenge> findUserActiveChallenges(int userId);

    List<UserChallenge> findUserChallengeHistory(int userId);

    boolean joinChallenge(int userId, int challengeId);

    boolean leaveChallenge(int userId, int challengeId);

    boolean updateChallengeProgress(int userId, int challengeId, int addedProgress);

    boolean isUserEnrolled(int userId, int challengeId);

    int countActiveChallenges();

    int countTotalParticipationsAllUsers();
}
