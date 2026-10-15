package com.fitness.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface abstracting database connection acquisition.
 * Demonstrates Java Functional Interfaces and clean Dependency Injection.
 */
@FunctionalInterface
public interface ConnectionProvider {

    Connection getConnection() throws SQLException;
}
