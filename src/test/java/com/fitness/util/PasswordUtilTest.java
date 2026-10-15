package com.fitness.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated unit tests verifying BCrypt password hashing and verification.
 */
class PasswordUtilTest {

    @Test
    @DisplayName("Hash should produce valid non-empty 60-character BCrypt hash")
    void testHashPasswordValid() {
        String rawPassword = "SecurePassword123!";
        String hash = PasswordUtil.hashPassword(rawPassword);

        assertNotNull(hash, "Password hash must not be null");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"),
                "Hash must follow standard BCrypt prefix format");
        assertEquals(60, hash.length(), "Standard BCrypt hash must be 60 characters long");
    }

    @Test
    @DisplayName("Salting should ensure two identical passwords yield different hashes")
    void testSaltGeneratesUniqueHashes() {
        String rawPassword = "SamePassword456";
        String hash1 = PasswordUtil.hashPassword(rawPassword);
        String hash2 = PasswordUtil.hashPassword(rawPassword);

        assertNotEquals(hash1, hash2, "Unique salts must result in different hash strings");
        assertTrue(PasswordUtil.checkPassword(rawPassword, hash1));
        assertTrue(PasswordUtil.checkPassword(rawPassword, hash2));
    }

    @Test
    @DisplayName("CheckPassword should return true for correct password and false for incorrect")
    void testCheckPasswordVerification() {
        String correct = "MyCorrectPassword789";
        String wrong = "WrongPassword123";
        String hash = PasswordUtil.hashPassword(correct);

        assertTrue(PasswordUtil.checkPassword(correct, hash), "Password verification should pass for correct input");
        assertFalse(PasswordUtil.checkPassword(wrong, hash), "Password verification should fail for incorrect input");
    }

    @Test
    @DisplayName("CheckPassword should gracefully handle null or empty inputs")
    void testCheckPasswordNullHandling() {
        assertFalse(PasswordUtil.checkPassword(null, "$2a$12$somevalidlengthmockhashstringwhichis60charslong123456789012"));
        assertFalse(PasswordUtil.checkPassword("password", null));
        assertFalse(PasswordUtil.checkPassword("password", ""));
        assertFalse(PasswordUtil.checkPassword("", "$2a$12$somevalidlengthmockhashstringwhichis60charslong123456789012"));
    }

    @Test
    @DisplayName("HashPassword should reject null or empty input with IllegalArgumentException")
    void testHashPasswordEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword("   "));
    }
}
