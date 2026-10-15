package com.fitness.model;

import java.sql.Timestamp;

/**
 * Domain model representing a user's participation and progress in a challenge.
 */
public class UserChallenge extends BaseEntity {

    private Integer userId;
    private Integer challengeId;
    private Timestamp joinedAt;
    private int currentProgress;
    private UserChallengeStatus status = UserChallengeStatus.IN_PROGRESS;
    private Timestamp completedAt;

    // Joined fields for display
    private String challengeTitle;
    private String challengeDescription;
    private ChallengeMetric targetMetric;
    private Integer targetGoal;
    private String badgeName;

    public UserChallenge() {
    }

    public UserChallenge(Integer id, Integer userId, Integer challengeId, int currentProgress, UserChallengeStatus status) {
        super(id);
        this.userId = userId;
        this.challengeId = challengeId;
        this.currentProgress = currentProgress;
        this.status = status != null ? status : UserChallengeStatus.IN_PROGRESS;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public UserChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(UserChallengeStatus status) {
        this.status = status;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }

    public ChallengeMetric getTargetMetric() {
        return targetMetric;
    }

    public void setTargetMetric(ChallengeMetric targetMetric) {
        this.targetMetric = targetMetric;
    }

    public Integer getTargetGoal() {
        return targetGoal;
    }

    public void setTargetGoal(Integer targetGoal) {
        this.targetGoal = targetGoal;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public int getProgressPercentage() {
        if (targetGoal == null || targetGoal <= 0) return 0;
        int pct = (int) Math.round(((double) currentProgress / targetGoal) * 100);
        return Math.min(100, Math.max(0, pct));
    }
}
