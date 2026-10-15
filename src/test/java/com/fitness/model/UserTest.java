package com.fitness.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated unit tests verifying User domain logic and BMI biometric calculations.
 */
class UserTest {

    @Test
    @DisplayName("CalculateBMI correctly computes BMI based on weight and height")
    void testCalculateBMI() {
        User user = new User();
        user.setWeightKg(new BigDecimal("70.0"));
        user.setHeightCm(new BigDecimal("175.0"));

        Double bmi = user.calculateBMI();
        assertNotNull(bmi);
        // BMI = 70 / (1.75 * 1.75) = 70 / 3.0625 = 22.857
        assertEquals(22.86, bmi, 0.05);
    }

    @Test
    @DisplayName("CalculateBMI returns null when height or weight is missing or zero")
    void testCalculateBMINullSafety() {
        User user = new User();
        assertNull(user.calculateBMI());

        user.setWeightKg(new BigDecimal("80.0"));
        assertNull(user.calculateBMI());

        user.setHeightCm(BigDecimal.ZERO);
        assertNull(user.calculateBMI());
    }

    @Test
    @DisplayName("IsAdmin and IsActive return correct booleans based on role and status")
    void testRoleAndStatusHelpers() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        assertTrue(admin.isAdmin());
        assertTrue(admin.isActive());

        User member = new User();
        member.setRole(UserRole.USER);
        member.setStatus(UserStatus.INACTIVE);

        assertFalse(member.isAdmin());
        assertFalse(member.isActive());
    }
}
