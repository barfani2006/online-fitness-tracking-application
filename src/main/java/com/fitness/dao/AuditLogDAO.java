package com.fitness.dao;

import com.fitness.model.AuditLog;

import java.util.List;

/**
 * Data Access Object interface for security and activity audit records.
 */
public interface AuditLogDAO extends GenericDAO<AuditLog, Integer> {

    List<AuditLog> findRecent(int limit);

    List<AuditLog> findByAction(String action, int limit);

    int countTotalLogs();
}
