package com.fitness.model;

import java.math.BigDecimal;

/**
 * Domain model representing an authenticated application user or administrator.
 */
public class User extends BaseEntity {

    private String fullName;
    private String email;
    private String passwordHash;
    private UserRole role = UserRole.USER;
    private UserStatus status = UserStatus.ACTIVE;
    private Integer age;
    private Gender gender = Gender.PREFER_NOT_TO_SAY;
    private BigDecimal weightKg;
    private BigDecimal heightCm;
    private FitnessLevel fitnessLevel = FitnessLevel.BEGINNER;

    public User() {
    }

    public User(Integer id, String fullName, String email, String passwordHash, UserRole role) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : UserRole.USER;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public FitnessLevel getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(FitnessLevel fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * Calculates Body Mass Index (BMI) if weight and height are present.
     */
    public Double calculateBMI() {
        if (weightKg == null || heightCm == null || heightCm.doubleValue() <= 0) {
            return null;
        }
        double heightInMeters = heightCm.doubleValue() / 100.0;
        return weightKg.doubleValue() / (heightInMeters * heightInMeters);
    }
}
