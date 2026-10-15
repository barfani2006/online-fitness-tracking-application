package com.fitness.dao;

import com.fitness.dao.impl.WorkoutDAOImpl;
import com.fitness.model.Workout;
import com.fitness.model.WorkoutIntensity;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure Java unit test verifying workout CRUD, updates, and cross-user isolation.
 */
public class WorkoutDAOTest {

    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        // Spin up in-memory H2 database with MySQL compatibility mode
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, full_name VARCHAR(100), email VARCHAR(150), " +
                    "password_hash VARCHAR(255), role VARCHAR(20), status VARCHAR(20), age INT, gender VARCHAR(20), " +
                    "weight_kg DECIMAL(5,2), height_cm DECIMAL(5,2), fitness_level VARCHAR(20), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS workout_categories (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), description VARCHAR(255), " +
                    "met_value DECIMAL(4,2), icon_name VARCHAR(50), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS workouts (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, category_id INT, " +
                    "activity_name VARCHAR(100) NOT NULL, workout_date DATE NOT NULL, duration_minutes INT NOT NULL, " +
                    "intensity VARCHAR(20) NOT NULL, calories_burned INT NOT NULL, notes TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("INSERT INTO users (id, full_name, email, password_hash, role, status) " +
                    "VALUES (101, 'Test User', 'test@example.com', 'hash123', 'USER', 'ACTIVE')");
            stmt.execute("INSERT INTO users (id, full_name, email, password_hash, role, status) " +
                    "VALUES (102, 'Other User', 'other@example.com', 'hash456', 'USER', 'ACTIVE')");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
        }
    }

    @Test
    void testWorkoutCreateReadUpdateDelete() {
        WorkoutDAOImpl workoutDAO = new WorkoutDAOImpl(dataSource::getConnection);

        // 1. Create Workout
        Workout w = new Workout();
        w.setUserId(101);
        w.setActivityName("Track Sprint Intervals");
        w.setWorkoutDate(LocalDate.now());
        w.setDurationMinutes(40);
        w.setIntensity(WorkoutIntensity.HIGH);
        w.setCaloriesBurned(380);
        w.setNotes("8x400m intervals");

        Workout saved = workoutDAO.save(w);
        assertNotNull(saved.getId(), "Workout ID should be generated upon insert");

        // 2. Read Workout
        Optional<Workout> retrieved = workoutDAO.findById(saved.getId());
        assertTrue(retrieved.isPresent(), "Workout should be retrievable by ID");
        assertEquals("Track Sprint Intervals", retrieved.get().getActivityName());
        assertEquals(40, retrieved.get().getDurationMinutes());

        // 3. Update Workout
        Workout toUpdate = retrieved.get();
        toUpdate.setDurationMinutes(50);
        toUpdate.setCaloriesBurned(460);
        toUpdate.setNotes("Increased to 10x400m");
        boolean updated = workoutDAO.update(toUpdate);
        assertTrue(updated, "Workout update should return true");

        Optional<Workout> updatedWorkout = workoutDAO.findById(saved.getId());
        assertEquals(50, updatedWorkout.get().getDurationMinutes());
        assertEquals(460, updatedWorkout.get().getCaloriesBurned());

        // 4. Data Isolation Check: User 102 should NOT be able to delete User 101's workout
        boolean unauthorizedDelete = workoutDAO.deleteByIdAndUserId(saved.getId(), 102);
        assertFalse(unauthorizedDelete, "User 102 cannot delete User 101's workout");

        // 5. Authorized Delete
        boolean authorizedDelete = workoutDAO.deleteByIdAndUserId(saved.getId(), 101);
        assertTrue(authorizedDelete, "User 101 can delete their own workout");

        Optional<Workout> afterDelete = workoutDAO.findById(saved.getId());
        assertTrue(afterDelete.isEmpty(), "Deleted workout should no longer exist");
    }
}
