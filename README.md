# Online Fitness Tracking Application

An online fitness tracking platform where users can record and manage workouts, track fitness progress, set personal goals, participate in fitness challenges, and receive personalized fitness recommendations. Administrators manage user accounts, review and approve fitness content, configure system settings, and monitor application activity and statistics.

Designed and developed as a complete Java Web Application for the 3rd Semester College Project adhering strictly to the Java Web Based Projects marking rubric (33 marks).

---

## 1. Project Objectives & Scope
- **User Workflows**:
  - Secure registration, login, profile management, and session handling.
  - CRUD operations on workout logs with category, intensity, duration, and calories burned.
  - Interactive fitness progress tracking with statistics, frequency, and visualizations.
  - Goal tracking with milestone updates and automatic progress calculation.
  - Community fitness challenges with join/leave functionality and participation records.
  - Rule-based personalized fitness recommendations (clearly labeled non-medical advice).
- **Administrator Workflows**:
  - User management (listing, role assignment, status toggling, deletion).
  - Fitness content moderation (create, review, approve, reject tips/articles).
  - System configuration & settings management.
  - Application-wide statistics and audit activity logging.

---

## 2. College Rubric Alignment (33 Marks)
1. **Problem Understanding and Solution Design (8 Marks)**:
   - Complete fitness domain model, clear user/admin separation, comprehensive requirements, and robust layered architecture.
2. **Core Java Concepts (10 Marks)**:
   - Object-Oriented Design (inheritance, abstract classes, polymorphism).
   - Interfaces (`UserDAO`, `WorkoutDAO`, `RecommendationService`, etc.).
   - Collections & Generics (`List<T>`, `Map<K, V>`, generic DAOs, generic wrappers).
   - Custom Exception hierarchy (`AppException`, `ValidationException`, `AuthenticationException`).
   - Multithreading with thread-safe executors for background audit logging and analytics calculations.
3. **Database Integration (JDBC) (8 Marks)**:
   - Dedicated DAO classes for all entities.
   - Prepared statements for 100% of parameterized SQL operations.
   - Database connection management and connection pooling (HikariCP).
   - Atomic multi-step database transactions (`commit()` / `rollback()`).
   - Normalised MySQL relational schema with primary keys, foreign keys, and indexes.
4. **Servlets and Web Integration (7 Marks)**:
   - Jakarta EE 10 / Servlet 6.0 request/response lifecycle.
   - JSP presentation layer with JSTL/EL and strict separation of presentation from business logic.
   - Session-based authentication, CSRF token validation, and role-based access control filters.
   - Comprehensive server-side input validation and error handling.

---

## 3. Technology Stack
- **Language**: Java 17+ (Java SE 17 / 21 / 26)
- **Web Layer**: Jakarta Servlet 6.0, Jakarta Server Pages (JSP) 3.1, JSTL 3.0
- **Database**: MySQL 8.0+ (Connector/J 8.3.0)
- **Connection Pool**: HikariCP 5.1.0
- **Security**: BCrypt Password Hashing (`org.mindrot:jbcrypt`), CSRF Tokens, Secure Sessions
- **Build Tool**: Apache Maven 3.9+
- **Application Server**: Apache Tomcat 10.1+ (Jakarta EE 10 compliant)
- **Testing**: JUnit 5, Mockito
