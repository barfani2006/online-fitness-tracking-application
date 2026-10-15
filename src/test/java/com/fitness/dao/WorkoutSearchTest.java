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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test verifying keyword searching, category filtering, date bounding, and sorting logic.
 */
public class WorkoutSearchTest {

    private JdbcDataSource dataSource;
    private WorkoutDAOImpl workoutDAO;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:searchdb;MODE=MySQL;DB_CLOSE_DELAY=-1");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS workout_categories (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), description VARCHAR(255), " +
                    "met_value DECIMAL(4,2), icon_name VARCHAR(50), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS workouts (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, category_id INT, " +
                    "activity_name VARCHAR(100) NOT NULL, workout_date DATE NOT NULL, duration_minutes INT NOT NULL, " +
                    "intensity VARCHAR(20) NOT NULL, calories_burned INT NOT NULL, notes TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("INSERT INTO workout_categories (id, name) VALUES (1, 'Running'), (2, 'Cycling'), (3, 'Swimming')");

            // Seed 3 workouts for user 201
            stmt.execute("INSERT INTO workouts (id, user_id, category_id, activity_name, workout_date, duration_minutes, intensity, calories_burned, notes) " +
                    "VALUES (1, 201, 1, 'Morning 5K Jog', '2026-10-01', 30, 'MEDIUM', 280, 'Park loop')");
            stmt.execute("INSERT INTO workouts (id, user_id, category_id, activity_name, workout_date, duration_minutes, intensity, calories_burned, notes) " +
                    "VALUES (2, 201, 2, 'Lakeshore Sprint Ride', '2026-10-05', 60, 'HIGH', 550, 'Windy day')");
            stmt.execute("INSERT INTO workouts (id, user_id, category_id, activity_name, workout_date, duration_minutes, intensity, calories_burned, notes) " +
                    "VALUES (3, 201, 3, 'Olympic Pool Freestyle', '2026-10-10', 45, 'HIGH', 400, 'Technique laps')");

            // Seed 1 workout for another user (user 202)
            stmt.execute("INSERT INTO workouts (id, user_id, category_id, activity_name, workout_date, duration_minutes, intensity, calories_burned, notes) " +
                    "VALUES (4, 202, 1, 'Evening Trail Jog', '2026-10-05', 35, 'LOW', 250, 'Forest trail')");
        }

        workoutDAO = new WorkoutDAOImpl(dataSource::getConnection);
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
        }
    }

    @Test
    void testKeywordSearch() {
        List<Workout> results = workoutDAO.searchWorkouts(201, "Sprint", null, null, null, "date", "DESC", 0, 10);
        assertEquals(1, results.size());
        assertEquals("Lakeshore Sprint Ride", results.get(0).getActivityName());
    }

    @Test
    void testCategoryFilter() {
        List<Workout> results = workoutDAO.searchWorkouts(201, null, 1, null, null, "date", "DESC", 0, 10);
        assertEquals(1, results.size());
        assertEquals("Morning 5K Jog", results.get(0).getActivityName());
    }

    @Test
    void testDateRangeFilter() {
        LocalDate start = LocalDate.of(2026, 10, 4);
        LocalDate end = LocalDate.of(2026, 10, 8);
        List<Workout> results = workoutDAO.searchWorkouts(201, null, null, start, end, "date", "DESC", 0, 10);
        assertEquals(1, results.size());
        assertEquals("Lakeshore Sprint Ride", results.get(0).getActivityName());
    }

    @Test
    void testSortingByDurationDescending() {
        List<Workout> results = workoutDAO.searchWorkouts(201, null, null, null, null, "duration", "DESC", 0, 10);
        assertEquals(3, results.size());
        assertEquals(60, results.get(0).getDurationMinutes());
        assertEquals(45, results.get(1).getDurationMinutes());
        assertEquals(30, results.get(2).getDurationMinutes());
    }
}
