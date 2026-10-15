# System Specification & Architecture

## System Modules
1. **Authentication & Session Module**:
   - `RegisterServlet`, `LoginServlet`, `LogoutServlet`
   - Password hashing via BCrypt (work factor 12)
   - `AuthFilter` and `AdminFilter` protecting URLs

2. **Workout Management Module**:
   - `WorkoutServlet`, `WorkoutDAO`
   - Attributes: ID, UserID, Category, Activity Name, Date, Duration (minutes), Calories Burned, Intensity (Low, Medium, High, Extreme), Notes
   - Sorting, filtering by category and date range, search by keyword

3. **Goal Tracking Module**:
   - `GoalServlet`, `GoalDAO`
   - Attributes: Goal Type (Weight Loss, Workout Frequency, Total Duration, Strength), Target Value, Current Progress, Deadline, Status (ACTIVE, COMPLETED, EXPIRED)

4. **Community Challenges Module**:
   - `ChallengeServlet`, `ChallengeDAO`
   - Browse challenges, join/leave, track milestone completion percentages

5. **Personalized Recommendations Engine**:
   - Rule-based algorithmic evaluator checking workout consistency, rest day recommendations, goal alignment, and intensity distribution.
   - Non-medical disclaimer included on all recommendation outputs.

6. **Administration & Analytics Module**:
   - `AdminDashboardServlet`, `UserManagementServlet`, `ContentModerationServlet`, `SettingsServlet`
   - Real-time aggregation of workouts, active users, goal completion rates, and system audit logs.

7. **Background Threading Module**:
   - `AsyncAuditLogger`: Thread pool for non-blocking asynchronous security and audit log persistence.
