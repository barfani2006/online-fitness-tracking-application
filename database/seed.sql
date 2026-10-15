-- =====================================================================
-- Online Fitness Tracking Application
-- Realistic Seed Data
-- =====================================================================

USE `fitness_tracker_db`;

-- 1. Insert Initial Users
-- Admin: admin@fitness.com / Admin@123
-- User 1: john@fitness.com / User@123
-- User 2: sarah@fitness.com / User@123
INSERT INTO `users` (`id`, `full_name`, `email`, `password_hash`, `role`, `status`, `age`, `gender`, `weight_kg`, `height_cm`, `fitness_level`) VALUES
(1, 'System Administrator', 'admin@fitness.com', '$2a$12$c9lhq/rt1FvLB.nOyyatOu4hOQbRHz9sxp5OFnz/Keyq7x1RxwM/.', 'ADMIN', 'ACTIVE', 32, 'OTHER', 72.0, 175.0, 'ADVANCED'),
(2, 'John Doe', 'john@fitness.com', '$2a$12$SF3VcDj8F2ZZfO3A93lq8OqpZsNlFj2Le6Sh/pwimsNmtDkisBzky', 'USER', 'ACTIVE', 24, 'MALE', 78.5, 180.0, 'INTERMEDIATE'),
(3, 'Sarah Jenkins', 'sarah@fitness.com', '$2a$12$SF3VcDj8F2ZZfO3A93lq8OqpZsNlFj2Le6Sh/pwimsNmtDkisBzky', 'USER', 'ACTIVE', 28, 'FEMALE', 62.0, 168.0, 'BEGINNER')
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`);

-- 2. Insert Standard Workout Categories
INSERT INTO `workout_categories` (`id`, `name`, `description`, `met_value`, `icon_name`) VALUES
(1, 'Running', 'Outdoor jogging and endurance distance running', 8.50, 'running'),
(2, 'Cycling', 'Outdoor cycling, mountain biking, or stationary spin', 7.00, 'bicycle'),
(3, 'Strength Training', 'Resistance training, weightlifting, barbell & dumbbell exercises', 5.50, 'dumbbell'),
(4, 'Swimming', 'Freestyle, breaststroke laps for full-body aerobic conditioning', 7.50, 'swimming'),
(5, 'Yoga & Mobility', 'Hatha, Vinyasa yoga flows, core stability, and deep stretching', 3.00, 'spa'),
(6, 'HIIT', 'High-Intensity Interval Training with bursts of max aerobic effort', 9.50, 'bolt'),
(7, 'Walking', 'Brisk aerobic walking and hill climbs', 3.80, 'walking'),
(8, 'Pilates', 'Core-strengthening and postural realignment movements', 4.00, 'heart')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 3. Insert Workouts for John Doe (user_id = 2) and Sarah (user_id = 3)
INSERT INTO `workouts` (`user_id`, `category_id`, `activity_name`, `workout_date`, `duration_minutes`, `intensity`, `calories_burned`, `notes`) VALUES
(2, 1, 'Morning 5K Park Run', DATE_SUB(CURDATE(), INTERVAL 6 DAY), 32, 'HIGH', 340, 'Paced at 6:24/km. Felt energetic throughout.'),
(2, 3, 'Chest & Triceps Hypertrophy', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 50, 'HIGH', 380, 'Bench press 4x8 @ 75kg, Incline dumbbell press, Dips.'),
(2, 5, 'Recovery Vinyasa Flow', DATE_SUB(CURDATE(), INTERVAL 4 DAY), 30, 'LOW', 110, 'Focus on hip openers and hamstrings mobility.'),
(2, 6, 'Full-Body HIIT Circuit', DATE_SUB(CURDATE(), INTERVAL 3 DAY), 25, 'EXTREME', 320, 'Tabata intervals: burpees, kettlebell swings, box jumps.'),
(2, 2, 'Evening Lakeshore Ride', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 45, 'MEDIUM', 390, 'Average speed 22 km/h. Great weather.'),
(2, 3, 'Back & Biceps Heavy Day', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 55, 'HIGH', 410, 'Deadlifts 4x6 @ 110kg, Pull-ups 4x10, Barbell rows.'),
(2, 1, 'Interval Sprint Track Session', CURDATE(), 35, 'EXTREME', 420, '400m repeats with 90s walk recovery.'),
(3, 7, 'Brisk Morning Walk', DATE_SUB(CURDATE(), INTERVAL 4 DAY), 40, 'LOW', 170, 'Walked around neighborhood trail.'),
(3, 5, 'Beginner Yoga Sequence', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 35, 'LOW', 130, 'Breathing exercises and foundational postures.'),
(3, 4, 'Freestyle Lap Swimming', CURDATE(), 30, 'MEDIUM', 260, '12 laps with short breaks between sets.');

-- 4. Insert Fitness Goals
INSERT INTO `fitness_goals` (`user_id`, `title`, `goal_type`, `target_value`, `current_value`, `unit`, `start_date`, `target_date`, `status`) VALUES
(2, 'Monthly Workout Frequency', 'WORKOUT_COUNT', 20.00, 7.00, 'workouts', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'ACTIVE'),
(2, 'Burn 10,000 Calories', 'CALORIES_BURNED', 10000.00, 2370.00, 'kcal', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'ACTIVE'),
(2, 'Reach 75kg Target Bodyweight', 'WEIGHT_TARGET', 75.00, 78.50, 'kg', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'ACTIVE'),
(3, 'Exercise 12 Times This Month', 'WORKOUT_COUNT', 12.00, 3.00, 'workouts', DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'ACTIVE');

-- 5. Insert Fitness Challenges
INSERT INTO `fitness_challenges` (`id`, `title`, `description`, `target_metric`, `target_goal`, `duration_days`, `badge_name`, `start_date`, `end_date`, `created_by`) VALUES
(1, '30-Day Consistency Streak', 'Log at least 20 workouts over 30 days to establish an unbreakable fitness habit.', 'WORKOUT_COUNT', 20, 30, 'Iron Will Medal', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 1),
(2, '1,000 Minutes of Movement', 'Accumulate 1,000 total active exercise minutes to elevate cardiovascular endurance.', 'TOTAL_MINUTES', 1000, 30, 'Endurance Titan', DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 1),
(3, '5,000 Calorie Burn Sprint', 'Torch 5,000 active calories through high-energy workouts and sports.', 'CALORIES', 5000, 14, 'Calorie Crusher', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 6. Insert User Challenge Participation
INSERT INTO `user_challenges` (`user_id`, `challenge_id`, `joined_at`, `current_progress`, `status`) VALUES
(2, 1, DATE_SUB(CURDATE(), INTERVAL 8 DAY), 7, 'IN_PROGRESS'),
(2, 2, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 272, 'IN_PROGRESS'),
(3, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 3, 'IN_PROGRESS')
ON DUPLICATE KEY UPDATE `current_progress` = VALUES(`current_progress`);

-- 7. Insert Curated Fitness Content
INSERT INTO `fitness_content` (`title`, `content`, `category`, `author_id`, `status`) VALUES
('The Science of Progressive Overload', 'Progressive overload involves gradually increasing the weight, frequency, or number of repetitions in your strength training routine. This challenges your musculoskeletal system and stimulates sustained muscular adaptation, growth, and neurological efficiency. Ensure adequate 48-hour recovery between identical muscle group workouts.', 'WORKOUT', 1, 'APPROVED'),
('Optimal Hydration & Electrolyte Timing', 'Water regulates core temperature, lubricates joints, and transports nutrients to active muscles. During moderate-to-vigorous exercise exceeding 45 minutes, aim for 150-250 ml of fluid every 15-20 minutes, including trace sodium and potassium for sustained muscular contraction and cramp prevention.', 'NUTRITION', 1, 'APPROVED'),
('Sleep Hygiene and Muscular Recovery', 'Growth hormone secretion peaks during slow-wave non-REM deep sleep. Prioritize 7 to 9 hours of uninterrupted sleep in a cool, dark environment to accelerate muscle protein synthesis, restore glycogen stores, and manage cortisol levels.', 'RECOVERY', 1, 'APPROVED'),
('High-Protein Smoothie for Post-Workout Recovery', 'Blend 1 scoop whey or plant protein, 1 ripe banana, 200ml almond milk, and 1 tbsp chia seeds within 60 minutes after intense lifting to support muscle protein synthesis.', 'NUTRITION', 2, 'PENDING');

-- 8. Insert Application Settings
INSERT INTO `app_settings` (`setting_key`, `setting_value`, `description`) VALUES
('app.name', 'PulseFit Tracker', 'Application public brand title'),
('app.registration.enabled', 'true', 'Allow new public user signups'),
('app.maintenance.mode', 'false', 'Enable maintenance mode for general users'),
('app.max.workout.duration', '360', 'Maximum allowable single workout duration in minutes'),
('app.recommendations.engine', 'RULE_BASED', 'Active recommendation engine algorithm')
ON DUPLICATE KEY UPDATE `setting_value` = VALUES(`setting_value`);

-- 9. Insert Initial Audit Logs
INSERT INTO `audit_logs` (`user_id`, `action`, `entity_type`, `entity_id`, `details`, `ip_address`) VALUES
(1, 'SYSTEM_INIT', 'SYSTEM', 1, 'System initialized with baseline schema, categories, and administrator credentials', '127.0.0.1'),
(1, 'CONTENT_APPROVE', 'FITNESS_CONTENT', 1, 'Administrator approved article: The Science of Progressive Overload', '127.0.0.1'),
(2, 'USER_REGISTER', 'USER', 2, 'New user registered: John Doe (john@fitness.com)', '127.0.0.1');
