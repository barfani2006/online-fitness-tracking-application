package com.fitness.model;

import java.time.LocalDate;

/**
 * Domain model representing a fitness challenge.
 */
public class FitnessChallenge extends BaseEntity {

    private String title;
    private String description;
    private ChallengeMetric targetMetric = ChallengeMetric.WORKOUT_COUNT;
    private Integer targetGoal;
    private Integer durationDays;
    private String badgeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer createdBy;

    // Transient attributes for UI presentation
    private int participantCount;
    private boolean userJoined;
    private int userProgress;
    private String userStatus;

    public FitnessChallenge() {
    }

    public FitnessChallenge(Integer id, String title, String description, ChallengeMetric targetMetric,
                            Integer targetGoal, Integer durationDays, String badgeName,
                            LocalDate startDate, LocalDate endDate, Integer createdBy) {
        super(id);
        this.title = title;
        this.description = description;
        this.targetMetric = targetMetric != null ? targetMetric : ChallengeMetric.WORKOUT_COUNT;
        this.targetGoal = targetGoal;
        this.durationDays = durationDays;
        this.badgeName = badgeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public boolean isUserJoined() {
        return userJoined;
    }

    public void setUserJoined(boolean userJoined) {
        this.userJoined = userJoined;
    }

    public int getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(int userProgress) {
        this.userProgress = userProgress;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return (startDate == null || !today.isBefore(startDate)) &&
                (endDate == null || !today.isAfter(endDate));
    }
}
