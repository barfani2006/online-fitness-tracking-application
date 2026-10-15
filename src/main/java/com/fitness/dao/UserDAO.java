package com.fitness.dao;

import com.fitness.model.User;
import com.fitness.model.UserRole;
import com.fitness.model.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDAO extends GenericDAO<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean emailExists(String email);

    boolean updatePassword(Integer userId, String newPasswordHash);

    boolean updateProfile(User user);

    boolean updateStatus(Integer userId, UserStatus status);

    List<User> searchUsers(String query, UserRole role, UserStatus status, int offset, int limit);

    int countUsers(String query, UserRole role, UserStatus status);

    int countTotalUsers();
}
