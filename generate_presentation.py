import sys
import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

def create_deck():
    prs = Presentation()
    # 16:9 widescreen
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6]

    # Color Palette
    DARK_NAVY = RGBColor(15, 23, 42)      # #0F172A
    CARD_BG = RGBColor(255, 255, 255)     # #FFFFFF
    PAGE_BG = RGBColor(248, 250, 252)    # #F8FAFC
    TEAL_PRIMARY = RGBColor(13, 148, 136) # #0D9488
    ROSE_ACCENT = RGBColor(225, 29, 72)   # #E11D48
    BLUE_ACCENT = RGBColor(37, 99, 235)   # #2563EB
    TEXT_DARK = RGBColor(15, 23, 42)
    TEXT_MUTED = RGBColor(100, 116, 139)  # #64748B
    BORDER_COLOR = RGBColor(226, 232, 240)# #E2E8F0
    CARD_BORDER = RGBColor(203, 213, 225)

    def add_header(slide, title_text, category_text="PULSEFIT — JAVA WEB PROJECT"):
        # Header banner background
        top_bar = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(1.1))
        top_bar.fill.solid()
        top_bar.fill.fore_color.rgb = DARK_NAVY
        top_bar.line.color.rgb = DARK_NAVY

        # Accent stripe
        stripe = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(1.05), Inches(13.333), Inches(0.06))
        stripe.fill.solid()
        stripe.fill.fore_color.rgb = TEAL_PRIMARY
        stripe.line.color.rgb = TEAL_PRIMARY

        # Category tag
        tx_cat = slide.shapes.add_textbox(Inches(0.8), Inches(0.12), Inches(11.5), Inches(0.3))
        tf_cat = tx_cat.text_frame
        tf_cat.word_wrap = True
        p_cat = tf_cat.paragraphs[0]
        p_cat.text = category_text.upper()
        p_cat.font.size = Pt(10)
        p_cat.font.bold = True
        p_cat.font.color.rgb = TEAL_PRIMARY

        # Title
        tx_title = slide.shapes.add_textbox(Inches(0.8), Inches(0.38), Inches(11.5), Inches(0.6))
        tf_title = tx_title.text_frame
        tf_title.word_wrap = True
        p_title = tf_title.paragraphs[0]
        p_title.text = title_text
        p_title.font.size = Pt(22)
        p_title.font.bold = True
        p_title.font.color.rgb = RGBColor(255, 255, 255)

        # Slide background fill
        bg = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(1.11), Inches(13.333), Inches(6.39))
        bg.fill.solid()
        bg.fill.fore_color.rgb = PAGE_BG
        bg.line.fill.background()

    def add_card(slide, left, top, width, height, title="", title_color=DARK_NAVY, bg_color=CARD_BG, border_color=CARD_BORDER):
        card = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
        card.fill.solid()
        card.fill.fore_color.rgb = bg_color
        card.line.color.rgb = border_color
        card.line.width = Pt(1)

        if title:
            tb = slide.shapes.add_textbox(left + Inches(0.2), top + Inches(0.15), width - Inches(0.4), Inches(0.45))
            tf = tb.text_frame
            tf.word_wrap = True
            p = tf.paragraphs[0]
            p.text = title
            p.font.size = Pt(14)
            p.font.bold = True
            p.font.color.rgb = title_color
        return card

    # =========================================================================
    # SLIDE 1: Title Slide
    # =========================================================================
    s1 = prs.slides.add_slide(blank_layout)
    bg1 = s1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
    bg1.fill.solid()
    bg1.fill.fore_color.rgb = DARK_NAVY
    bg1.line.fill.background()

    # Brand badge
    b1 = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.0), Inches(1.3), Inches(2.2), Inches(0.5))
    b1.fill.solid()
    b1.fill.fore_color.rgb = TEAL_PRIMARY
    b1.line.fill.background()
    p = b1.text_frame.paragraphs[0]
    p.text = "COLLEGE REVIEW 1"
    p.alignment = PP_ALIGN.CENTER
    p.font.size = Pt(12)
    p.font.bold = True
    p.font.color.rgb = RGBColor(255, 255, 255)

    tb = s1.shapes.add_textbox(Inches(1.0), Inches(1.9), Inches(11.3), Inches(2.2))
    tf = tb.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = "PulseFit - Online Fitness Tracking Application"
    p.font.size = Pt(36)
    p.font.bold = True
    p.font.color.rgb = RGBColor(255, 255, 255)

    p2 = tf.add_paragraph()
    p2.text = "A Complete, Enterprise Java Web Application Demonstrating Core OOP, Pure JDBC, Multithreaded Async Logging, and Jakarta Servlet/JSP Architecture"
    p2.font.size = Pt(16)
    p2.font.color.rgb = RGBColor(203, 213, 225)
    p2.space_before = Pt(14)

    # Info cards at bottom
    specs = [
        ("Student Project", "3rd Semester B.Tech / MCA", "Java Web Based Project"),
        ("Marking Rubric", "33 Marks Total", "Problem 8 | Java 10 | JDBC 8 | Web 7"),
        ("Tech Stack", "Java 17+ / Servlet 6.0", "MySQL 8.0, HikariCP, Tomcat 10.1"),
        ("Verification", "23/23 Tests Passed", "30 Chronological Git Commits")
    ]
    for i, (head, sub, detail) in enumerate(specs):
        left = Inches(1.0 + i * 2.85)
        c = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, Inches(4.7), Inches(2.7), Inches(1.8))
        c.fill.solid()
        c.fill.fore_color.rgb = RGBColor(30, 41, 59)
        c.line.color.rgb = RGBColor(51, 65, 85)
        tf_c = c.text_frame
        tf_c.word_wrap = True
        
        p = tf_c.paragraphs[0]
        p.text = head.upper()
        p.font.size = Pt(10)
        p.font.bold = True
        p.font.color.rgb = TEAL_PRIMARY
        
        p = tf_c.add_paragraph()
        p.text = sub
        p.font.size = Pt(14)
        p.font.bold = True
        p.font.color.rgb = RGBColor(255, 255, 255)
        p.space_before = Pt(6)

        p = tf_c.add_paragraph()
        p.text = detail
        p.font.size = Pt(11)
        p.font.color.rgb = RGBColor(148, 163, 184)
        p.space_before = Pt(4)

    # =========================================================================
    # SLIDE 2: Problem Statement & Motivation
    # =========================================================================
    s2 = prs.slides.add_slide(blank_layout)
    add_header(s2, "Problem Statement & Project Motivation", "Problem Understanding & Solution Design — 8 Marks")

    add_card(s2, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "The Real-World Problem", ROSE_ACCENT)
    tb = s2.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    points = [
        ("Disorganized Training Records", "Fitness enthusiasts frequently rely on fragmented physical notebooks or generic note apps, losing historical trends, cardiovascular records, and progression data."),
        ("Lack of Quantitative Insights", "Without automated caloric and duration calculations, trainees struggle to evaluate progressive overload, workout consistency, or recovery gaps."),
        ("Isolated Fitness Journeys", "Personal goal setting and structured community challenges are usually locked behind costly proprietary mobile apps filled with ads and bloatware."),
        ("Need for Non-Medical Guidance", "Users need contextual guidance based on training consistency without dangerous or misleading diagnostic claims.")
    ]
    for i, (title, desc) in enumerate(points):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = f"{i+1}. {title}"
        p.font.bold = True
        p.font.size = Pt(13)
        p.font.color.rgb = DARK_NAVY
        if i > 0: p.space_before = Pt(12)
        p_sub = tf.add_paragraph()
        p_sub.text = desc
        p_sub.font.size = Pt(11)
        p_sub.font.color.rgb = TEXT_MUTED

    add_card(s2, Inches(6.8), Inches(1.4), Inches(5.7), Inches(5.4), "The PulseFit Solution & Academic Justification", TEAL_PRIMARY)
    tb = s2.shapes.add_textbox(Inches(7.0), Inches(2.0), Inches(5.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    sol_points = [
        ("Full-Stack Web Architecture", "Built from foundational Java technologies (Jakarta Servlet 6.0, JSP 3.1, JSTL) demonstrating exact college curriculum requirements without heavy frameworks."),
        ("Distinct Role-Based Workspaces", "Separate, protected user and administrator portals with rigorous role-based access control filters and session management."),
        ("Pure JDBC with PreparedStatements", "100% SQL injection immune with dedicated Data Access Objects (DAOs) and high-performance HikariCP connection pooling."),
        ("Asynchronous Multithreaded Logging", "Multithreaded Producer-Consumer audit logging engine with BlockingQueue, ensuring zero HTTP latency on administrative and security audits.")
    ]
    for i, (title, desc) in enumerate(sol_points):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = f"• {title}"
        p.font.bold = True
        p.font.size = Pt(13)
        p.font.color.rgb = DARK_NAVY
        if i > 0: p.space_before = Pt(12)
        p_sub = tf.add_paragraph()
        p_sub.text = desc
        p_sub.font.size = Pt(11)
        p_sub.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 3: Project Objectives & Scope
    # =========================================================================
    s3 = prs.slides.add_slide(blank_layout)
    add_header(s3, "Project Objectives & Scope of Work", "Requirements & Specifications")

    cols = [
        ("Core Objectives", TEAL_PRIMARY, [
            "Demonstrate College Java Rubric (33 Marks) with 100% working functionality.",
            "Implement secure authentication with BCrypt hashing and cryptographic CSRF tokens.",
            "Provide full CRUD workout logging with automatic MET-based calorie estimation.",
            "Generate actionable visual progress metrics and weekly growth rate percentages.",
            "Enable community fitness challenges and quantifiable milestone goals."
        ]),
        ("System Boundaries & Scope", BLUE_ACCENT, [
            "Pure Java MVC pattern: Servlets handle controllers, JSPs handle views.",
            "Dedicated DAO Layer: Encapsulates all SQL statements cleanly.",
            "Connection Pool: HikariCP with active pool size = 10 for enterprise scalability.",
            "Cross-User Isolation: All queries enforce WHERE user_id = ? boundaries.",
            "Responsive Web UI: Fully functional across desktop, tablet, and mobile browsers."
        ]),
        ("Key Non-Functional Goals", ROSE_ACCENT, [
            "Security: Strict XSS sanitization, prepared statements, and session regeneration.",
            "Reliability: Automatic ACID transaction rollback on multi-table updates.",
            "Transparency: Explicit non-medical disclaimer attached to all recommendations.",
            "Maintainability: Modular packages (model, dao, service, servlet, filter, util).",
            "Extensibility: Standardized GenericDAO<T, ID> extensible for new features."
        ])
    ]
    for i, (title, color, items) in enumerate(cols):
        left = Inches(0.8 + i * 4.0)
        add_card(s3, left, Inches(1.4), Inches(3.7), Inches(5.4), title, color)
        tb = s3.shapes.add_textbox(left + Inches(0.2), Inches(2.0), Inches(3.3), Inches(4.5))
        tf = tb.text_frame
        tf.word_wrap = True
        for j, item in enumerate(items):
            p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
            p.text = f"✔  {item}"
            p.font.size = Pt(11)
            p.font.color.rgb = TEXT_DARK
            if j > 0: p.space_before = Pt(10)

    # =========================================================================
    # SLIDE 4: User vs Administrator Role Separation
    # =========================================================================
    s4 = prs.slides.add_slide(blank_layout)
    add_header(s4, "Role-Based Access Control (RBAC) & Architecture", "Security & System Boundaries")

    add_card(s4, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Standard User Workspace (/dashboard, /workouts...)", TEAL_PRIMARY)
    tb = s4.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    user_features = [
        "Workout Management: Create, edit, search, filter, and delete personal workouts.",
        "Analytics Dashboard: Weekly growth rate, consistency adherence, and Chart.js trends.",
        "Goal Milestones: Target tracking with dynamic completion percentage.",
        "Community Challenges: Enroll in active challenges, record daily progress.",
        "Rule-Based Guidance: Algorithmic tips tailored to workout frequency and rest.",
        "Profile & Biometrics: BMI calculation and verified password change."
    ]
    for j, it in enumerate(user_features):
        p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
        p.text = f"• {it}"
        p.font.size = Pt(12)
        p.font.color.rgb = TEXT_DARK
        if j > 0: p.space_before = Pt(12)

    add_card(s4, Inches(6.8), Inches(1.4), Inches(5.7), Inches(5.4), "Superuser Administrator Console (/admin/*)", ROSE_ACCENT)
    tb = s4.shapes.add_textbox(Inches(7.0), Inches(2.0), Inches(5.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    admin_features = [
        "User Directory Administration: Search, paginate, edit roles, toggle status, delete accounts.",
        "Content Moderation Queue: Review community tips, approve/reject with feedback reason.",
        "System Settings Manager: Configure global parameters (maintenance mode, limits).",
        "Platform-Wide Statistics: Aggregated metrics across all registered platform users.",
        "Session Monitoring: Real-time active HTTP session tracking via SessionListener.",
        "Activity Audit Log: Inspect security and administrative events recorded asynchronously."
    ]
    for j, it in enumerate(admin_features):
        p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
        p.text = f"• {it}"
        p.font.size = Pt(12)
        p.font.color.rgb = TEXT_DARK
        if j > 0: p.space_before = Pt(12)

    # =========================================================================
    # SLIDE 5: Functional Requirements — User Module
    # =========================================================================
    s5 = prs.slides.add_slide(blank_layout)
    add_header(s5, "User Module — Detailed Functional Workflows", "Core User Functionality")

    workflows = [
        ("Workout CRUD & MET Estimation", TEAL_PRIMARY, [
            "Log workouts with category, date, duration, intensity (Low/Med/High/Extreme).",
            "Automatic MET-based calorie estimation in real-time as user types duration.",
            "Multi-criteria filtering: category dropdown, intensity filter, sort order (date/duration/calories)."
        ]),
        ("Progress Analytics & Charts", BLUE_ACCENT, [
            "Calculates total workouts, hours exercised, calories burned, average session length.",
            "Weekly Growth Rate: percentage comparison against the previous 7-day period.",
            "Adherence Score: 7-day consistency percentage; interactive Chart.js line and doughnut visualizations."
        ]),
        ("Goals & Community Challenges", DARK_NAVY, [
            "Quantitative milestones: target workouts, duration, calories burned, or target bodyweight.",
            "Visual milestone progress bar with real-time percentage completion calculation.",
            "Browse community challenges, join with ACID transaction rollback, and track daily progress."
        ]),
        ("Algorithmic Guidance & Profile", ROSE_ACCENT, [
            "Inactivity alert triggered if gap between workouts >= 4 days.",
            "Recovery warning generated if >= 2 consecutive high/extreme intensity workouts.",
            "Prominent Non-Medical Disclaimer attached to all suggestions; biometric BMI calculator."
        ])
    ]
    for i, (title, color, items) in enumerate(workflows):
        col = i % 2
        row = i // 2
        left = Inches(0.8 + col * 5.9)
        top = Inches(1.4 + row * 2.8)
        add_card(s5, left, top, Inches(5.7), Inches(2.6), title, color)
        tb = s5.shapes.add_textbox(left + Inches(0.2), top + Inches(0.55), Inches(5.3), Inches(1.9))
        tf = tb.text_frame
        tf.word_wrap = True
        for j, it in enumerate(items):
            p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
            p.text = f"✔  {it}"
            p.font.size = Pt(11)
            p.font.color.rgb = TEXT_DARK
            if j > 0: p.space_before = Pt(6)

    # =========================================================================
    # SLIDE 6: Functional Requirements — Admin Module
    # =========================================================================
    s6 = prs.slides.add_slide(blank_layout)
    add_header(s6, "Admin Module — Oversight & Platform Management", "Administrative Functionality")

    admin_cards = [
        ("User Management Engine", BLUE_ACCENT, [
            "Search users by name, email, role, or active/inactive status.",
            "Paginated table displaying creation timestamp and workout count.",
            "One-click account deactivation (deactivated users cannot log in).",
            "Admin cannot delete their own account (prevents lockout)."
        ]),
        ("Content Moderation Queue", TEAL_PRIMARY, [
            "Curated repository of fitness guides, workout tips, and articles.",
            "One-click approval publishes content to member recommendations.",
            "Rejection dialog requiring mandatory feedback reason for author.",
            "Direct creation and modification of official guides by admins."
        ]),
        ("System Configuration & Settings", DARK_NAVY, [
            "Key-value settings stored in relational app_settings table.",
            "Configurable parameters: Maintenance mode, Max workout duration (mins), Password length.",
            "Live update with instant audit log capture."
        ]),
        ("Asynchronous Activity Monitoring", ROSE_ACCENT, [
            "Real-time event capture: LOGIN_SUCCESS, USER_CREATED, CONTENT_APPROVED, etc.",
            "Action filtering dropdown and chronological audit trail table.",
            "Zero HTTP overhead powered by background AsyncAuditLogger."
        ])
    ]
    for i, (title, color, items) in enumerate(admin_cards):
        col = i % 2
        row = i // 2
        left = Inches(0.8 + col * 5.9)
        top = Inches(1.4 + row * 2.8)
        add_card(s6, left, top, Inches(5.7), Inches(2.6), title, color)
        tb = s6.shapes.add_textbox(left + Inches(0.2), top + Inches(0.55), Inches(5.3), Inches(1.9))
        tf = tb.text_frame
        tf.word_wrap = True
        for j, it in enumerate(items):
            p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
            p.text = f"• {it}"
            p.font.size = Pt(11)
            p.font.color.rgb = TEXT_DARK
            if j > 0: p.space_before = Pt(6)

    # =========================================================================
    # SLIDE 7: Technical Stack & Architecture
    # =========================================================================
    s7 = prs.slides.add_slide(blank_layout)
    add_header(s7, "Technology Stack & Technical Decisions", "Architecture & Web Technologies — 7 Marks")

    tech_boxes = [
        ("Backend & Language", TEAL_PRIMARY, "Java 17+ LTS", [
            "Java SE 17, 21, and 26 compatible.",
            "Modern language features: records, switch expressions, streams, lambdas.",
            "Strong object-oriented class hierarchy."
        ]),
        ("Web Layer", BLUE_ACCENT, "Jakarta EE 10 / Servlet 6.0", [
            "Jakarta Servlet API 6.0 with @WebServlet.",
            "JSP 3.1 & JSTL 3.0 (Pure JSTL, 0 scriptlets).",
            "Custom session management & listeners."
        ]),
        ("Persistence & Pool", DARK_NAVY, "MySQL 8.0 + HikariCP", [
            "MySQL Connector/J 8.3.0.",
            "HikariCP 5.1.0 connection pool (10 connections).",
            "100% PreparedStatements for parameterized SQL."
        ]),
        ("Security & Utilities", ROSE_ACCENT, "BCrypt & CSRF Tokens", [
            "BCrypt password hashing (12 salt rounds).",
            "Per-session cryptographic CSRF tokens.",
            "SLF4J & Logback enterprise logging."
        ])
    ]
    for i, (layer, color, tech, bullet_items) in enumerate(tech_boxes):
        left = Inches(0.8 + i * 2.95)
        add_card(s7, left, Inches(1.4), Inches(2.8), Inches(5.4), layer, color)
        tb = s7.shapes.add_textbox(left + Inches(0.15), Inches(2.0), Inches(2.5), Inches(4.5))
        tf = tb.text_frame
        tf.word_wrap = True
        
        p = tf.paragraphs[0]
        p.text = tech
        p.font.size = Pt(13)
        p.font.bold = True
        p.font.color.rgb = DARK_NAVY

        for j, it in enumerate(bullet_items):
            p = tf.add_paragraph()
            p.text = f"• {it}"
            p.font.size = Pt(11)
            p.font.color.rgb = TEXT_MUTED
            p.space_before = Pt(8)

    # =========================================================================
    # SLIDE 8: Layered System Architecture
    # =========================================================================
    s8 = prs.slides.add_slide(blank_layout)
    add_header(s8, "Layered MVC Architecture & Request Lifecycle", "Software Engineering Design")

    layers = [
        ("1. Filter Interceptor Layer", ROSE_ACCENT, "EncodingFilter -> AuthenticationFilter -> AdminAuthorizationFilter",
         "Intercepts all HTTP traffic. Sets UTF-8 encoding, validates session authentication, enforces RBAC, and prevents unauthorized URL access with clean 401/403 errors."),
        ("2. Controller Layer (Servlets)", BLUE_ACCENT, "15 Dedicated Jakarta Servlets (@WebServlet mapping)",
         "Parses HTTP parameters, validates CSRF tokens, calls server-side validation utilities, invokes business services, and dispatches data to JSP views."),
        ("3. Service & Business Logic Layer", TEAL_PRIMARY, "ProgressService, RecommendationService, AsyncAuditLogger",
         "Encapsulates domain logic: calculates weekly growth %, adherence scores, evaluates rule-based recommendation triggers, and enqueues async audit events."),
        ("4. Data Access Object (DAO) Layer", DARK_NAVY, "GenericDAO<T, ID> -> UserDAO, WorkoutDAO, GoalDAO, ChallengeDAO...",
         "Pure JDBC implementation using HikariCP connection pool. Manages prepared statements, result sets, mapping to model entities, and transaction rollback.")
    ]
    for i, (name, color, comp, desc) in enumerate(layers):
        top = Inches(1.4 + i * 1.35)
        add_card(s8, Inches(0.8), top, Inches(11.7), Inches(1.2), name, color)
        tb = s8.shapes.add_textbox(Inches(1.0), top + Inches(0.4), Inches(11.3), Inches(0.7))
        tf = tb.text_frame
        tf.word_wrap = True
        
        p = tf.paragraphs[0]
        p.text = f"Components: {comp}"
        p.font.bold = True
        p.font.size = Pt(11)
        p.font.color.rgb = DARK_NAVY

        p_desc = tf.add_paragraph()
        p_desc.text = desc
        p_desc.font.size = Pt(10)
        p_desc.font.color.rgb = TEXT_MUTED
        p_desc.space_before = Pt(3)

    # =========================================================================
    # SLIDE 9: Database Schema & Relational Design
    # =========================================================================
    s9 = prs.slides.add_slide(blank_layout)
    add_header(s9, "Relational Database Schema (JDBC Integration — 8 Marks)", "Database Integration")

    tables = [
        ("users", "User accounts, password hashes, biometric data (height/weight/age), roles (USER/ADMIN), status."),
        ("workout_categories", "Reference table of standardized workout modalities (Running, Cycling, HIIT, Weights, Yoga)."),
        ("workouts", "Core exercise logs: user_id (FK), category_id (FK), duration, calories, intensity, date, notes."),
        ("fitness_goals", "User milestone targets: user_id (FK), goal_type, target_value, current_value, target_date."),
        ("fitness_challenges", "Community challenges: title, description, activity_type, target_days, start/end dates."),
        ("user_challenges", "Challenge enrollment junction table: user_id (FK), challenge_id (FK), progress_days, status."),
        ("fitness_content", "Educational guides and tips: author_id (FK), title, content, status (APPROVED/PENDING/REJECTED)."),
        ("audit_logs & app_settings", "Security/admin audit trail (user_id, action, entity, details, IP) and global key-value configuration.")
    ]
    for i, (tbl, desc) in enumerate(tables):
        col = i % 2
        row = i // 2
        left = Inches(0.8 + col * 5.9)
        top = Inches(1.4 + row * 1.35)
        add_card(s9, left, top, Inches(5.7), Inches(1.2), f"Table: {tbl}", DARK_NAVY)
        tb = s9.shapes.add_textbox(left + Inches(0.2), top + Inches(0.45), Inches(5.3), Inches(0.7))
        tf = tb.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = desc
        p.font.size = Pt(10)
        p.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 10: Security & Authentication Architecture
    # =========================================================================
    s10 = prs.slides.add_slide(blank_layout)
    add_header(s10, "Security, Authentication & Threat Mitigations", "Defensive Engineering")

    sec_items = [
        ("BCrypt Password Hashing", TEAL_PRIMARY,
         "Blowfish cipher with 12 salt rounds (PasswordUtil). Protects credentials against rainbow table and dictionary attacks. Plaintext passwords never persist."),
        ("Cryptographic CSRF Tokens", BLUE_ACCENT,
         "Unique cryptographically random tokens (CsrfUtil) stored in user sessions and injected as hidden form fields. Verified on all state-changing POST requests."),
        ("SQL Injection Prevention", DARK_NAVY,
         "100% Parameterized queries via java.sql.PreparedStatement. Dynamic user input is never concatenated directly into SQL queries."),
        ("Session Fixation Defense", ROSE_ACCENT,
         "Sessions are invalidated and regenerated upon successful login (session.invalidate() -> session = req.getSession(true)), thwarting session hijacking."),
        ("Cross-Site Scripting (XSS) Sanitization", TEAL_PRIMARY,
         "Server-side HTML entity escaping (ValidationUtil.escapeHtml()) and JSTL <c:out> escaping neutralize malicious HTML/JS payloads."),
        ("Insecure Direct Object Reference (IDOR)", BLUE_ACCENT,
         "Cross-user isolation: all workout/goal updates explicitly filter WHERE id = ? AND user_id = ?, preventing users from tampering with others' data.")
    ]
    for i, (title, color, desc) in enumerate(sec_items):
        col = i % 2
        row = i // 2
        left = Inches(0.8 + col * 5.9)
        top = Inches(1.4 + row * 1.8)
        add_card(s10, left, top, Inches(5.7), Inches(1.65), title, color)
        tb = s10.shapes.add_textbox(left + Inches(0.2), top + Inches(0.45), Inches(5.3), Inches(1.1))
        tf = tb.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = desc
        p.font.size = Pt(11)
        p.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 11: Workout & Progress Analytics Engine
    # =========================================================================
    s11 = prs.slides.add_slide(blank_layout)
    add_header(s11, "Workout & Progress Analytics Engine", "Data Analysis & Visualizations")

    add_card(s11, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Analytical Formulas & Algorithms", TEAL_PRIMARY)
    tb = s11.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    formulas = [
        ("MET Calorie Estimation", "Calories = MET * Weight(kg) * (Duration/60) * IntensityMultiplier. Estimated in real-time as users type."),
        ("Weekly Growth Rate (%)", "Growth % = ((ThisWeekMinutes - LastWeekMinutes) / LastWeekMinutes) * 100. Measures progression over 7-day windows."),
        ("Consistency Adherence Score (%)", "Score = (DaysWithWorkouts / 7) * 100. Rewards regular training cadence and habit formation."),
        ("Biometric BMI Calculation", "BMI = Weight(kg) / (Height(m))^2. Categorizes user status (Underweight, Normal, Overweight, Obese).")
    ]
    for i, (name, f_desc) in enumerate(formulas):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = f"{i+1}. {name}"
        p.font.bold = True
        p.font.size = Pt(12)
        p.font.color.rgb = DARK_NAVY
        if i > 0: p.space_before = Pt(12)
        p_sub = tf.add_paragraph()
        p_sub.text = f_desc
        p_sub.font.size = Pt(11)
        p_sub.font.color.rgb = TEXT_MUTED

    add_card(s11, Inches(6.8), Inches(1.4), Inches(5.7), Inches(5.4), "Visual Dashboard & Chart.js Integration", BLUE_ACCENT)
    tb = s11.shapes.add_textbox(Inches(7.0), Inches(2.0), Inches(5.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    chart_points = [
        ("Daily Duration Trend Chart", "Interactive line chart rendering daily active minutes over the past 7 days, allowing users to track peaks and rest days."),
        ("Modality Distribution Breakdown", "Doughnut chart illustrating workout variety across Running, Weights, HIIT, Cycling, and Yoga."),
        ("Dynamic Metric Cards", "Instant visual summary of Total Hours, Total Workouts, Calories Burned, and Average Workout Duration."),
        ("Zero-Dependency Native Assets", "Chart.js initialized via lightweight UMD bundle without bloated frontend npm frameworks.")
    ]
    for i, (name, c_desc) in enumerate(chart_points):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = f"• {name}"
        p.font.bold = True
        p.font.size = Pt(12)
        p.font.color.rgb = DARK_NAVY
        if i > 0: p.space_before = Pt(12)
        p_sub = tf.add_paragraph()
        p_sub.text = c_desc
        p_sub.font.size = Pt(11)
        p_sub.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 12: Goals & Community Challenges Workflow
    # =========================================================================
    s12 = prs.slides.add_slide(blank_layout)
    add_header(s12, "Goals, Community Challenges & ACID Transactions", "Core Workflows & Transactions")

    add_card(s12, Inches(0.8), Inches(1.4), Inches(5.6), Inches(5.4), "Personal Fitness Goals Workflow", TEAL_PRIMARY)
    tb = s12.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(5.2), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    goal_steps = [
        ("Target Definition", "Users establish quantifiable goals (Total Workouts, Duration Minutes, Calories Burned, or Target Bodyweight)."),
        ("Milestone Visualization", "Dynamic percentage bar (current / target * 100) displays incremental progress with color-coded badges."),
        ("Automatic Completion", "When current value meets or exceeds the target, status automatically transitions to COMPLETED."),
        ("Edit & Deletion Safeguards", "Users can update target deadlines or safely delete outdated milestones.")
    ]
    for i, (name, g_desc) in enumerate(goal_steps):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = f"✔ {name}"
        p.font.bold = True
        p.font.size = Pt(12)
        p.font.color.rgb = DARK_NAVY
        if i > 0: p.space_before = Pt(12)
        p_sub = tf.add_paragraph()
        p_sub.text = g_desc
        p_sub.font.size = Pt(11)
        p_sub.font.color.rgb = TEXT_MUTED

    add_card(s12, Inches(6.8), Inches(1.4), Inches(5.7), Inches(5.4), "Community Challenges & ACID Transactions", DARK_NAVY)
    tb = s12.shapes.add_textbox(Inches(7.0), Inches(2.0), Inches(5.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    chal_steps = [
        ("Structured Challenges", "Explore active community events (e.g. 30-Day Cardio Blast, Consistent 5K Runner, Yoga Mobility)."),
        ("Atomic Multi-Step Enrollment", "Uses manual JDBC transaction boundaries: connection.setAutoCommit(false), inserts junction record, verifies constraints, and commits."),
        ("Automated Rollback on Error", "If database failure occurs mid-transaction, DBConnectionManager.rollbackQuietly(conn) restores initial state."),
        ("Daily Progress Logging", "Users record active participation days with validation preventing duplicate daily logs.")
    ]
    for i, (name, c_desc) in enumerate(chal_steps):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = f"✔ {name}"
        p.font.bold = True
        p.font.size = Pt(12)
        p.font.color.rgb = DARK_NAVY
        if i > 0: p.space_before = Pt(12)
        p_sub = tf.add_paragraph()
        p_sub.text = c_desc
        p_sub.font.size = Pt(11)
        p_sub.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 13: Administrator Console & Content Moderation
    # =========================================================================
    s13 = prs.slides.add_slide(blank_layout)
    add_header(s13, "Content Moderation Queue & Admin Controls", "Administrator Operations")

    cards_13 = [
        ("Content Moderation Queue", TEAL_PRIMARY, [
            "Review submitted fitness articles, health guides, and workout routines.",
            "One-click approval publishes content to user recommendation feeds.",
            "Rejection modal captures structured rationale (e.g. unverified health claims)."
        ]),
        ("Live Session Monitoring", BLUE_ACCENT, [
            "SessionListener implements HttpSessionListener to track live logins.",
            "Displays real-time concurrent active session counts on Admin Dashboard.",
            "Thread-safe atomic counter (AtomicInteger) increments/decrements safely."
        ]),
        ("User Lifecycle Management", ROSE_ACCENT, [
            "Filter users by role, status, or search query with full pagination.",
            "Deactivate suspicious accounts without destroying workout history.",
            "Safeguard: Admin self-deletion blocked to prevent orphaned databases."
        ]),
        ("Dynamic System Configuration", DARK_NAVY, [
            "Manage platform parameters without restarting the web container.",
            "Controls maintenance mode, maximum session duration, and password policy.",
            "All changes logged with before/after state in audit records."
        ])
    ]
    for i, (title, color, items) in enumerate(cards_13):
        col = i % 2
        row = i // 2
        left = Inches(0.8 + col * 5.9)
        top = Inches(1.4 + row * 2.8)
        add_card(s13, left, top, Inches(5.7), Inches(2.6), title, color)
        tb = s13.shapes.add_textbox(left + Inches(0.2), top + Inches(0.55), Inches(5.3), Inches(1.9))
        tf = tb.text_frame
        tf.word_wrap = True
        for j, it in enumerate(items):
            p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
            p.text = f"• {it}"
            p.font.size = Pt(11)
            p.font.color.rgb = TEXT_DARK
            if j > 0: p.space_before = Pt(6)

    # =========================================================================
    # SLIDE 14: Core Java Concepts & Rubric Justification
    # =========================================================================
    s14 = prs.slides.add_slide(blank_layout)
    add_header(s14, "Core Java Concepts & Rubric Justification (10 Marks)", "Core Java Implementation")

    java_concepts = [
        ("Inheritance & OOP", TEAL_PRIMARY,
         "BaseEntity defines common primary keys, timestamps, and getters/setters inherited by User, Workout, FitnessGoal, FitnessChallenge, and AuditLog."),
        ("Polymorphism & Interfaces", BLUE_ACCENT,
         "Clean contract abstractions: GenericDAO<T, ID>, UserDAO, WorkoutDAO, RecommendationService, ConnectionProvider with swap-in implementations."),
        ("Collections & Generics", DARK_NAVY,
         "Extensive use of type-safe collections: List<Workout>, Map<String, Object>, Optional<User>, and GenericDAO<T, ID> for reusable CRUD operations."),
        ("Custom Exception Hierarchy", ROSE_ACCENT,
         "Application-specific unchecked exceptions: AppException base class with DatabaseException, ValidationException, and AuthenticationException."),
        ("Multithreading: AsyncAuditLogger", TEAL_PRIMARY,
         "Producer-Consumer pattern with BlockingQueue<AuditLog> and dedicated background daemon thread. Decouples HTTP request execution from disk/database I/O."),
        ("Concurrency & Thread Safety", BLUE_ACCENT,
         "Uses AtomicBoolean for worker lifecycle, AtomicInteger in SessionListener, and synchronized double-checked locking in singleton instances.")
    ]
    for i, (title, color, desc) in enumerate(java_concepts):
        col = i % 2
        row = i // 2
        left = Inches(0.8 + col * 5.9)
        top = Inches(1.4 + row * 1.8)
        add_card(s14, left, top, Inches(5.7), Inches(1.65), title, color)
        tb = s14.shapes.add_textbox(left + Inches(0.2), top + Inches(0.45), Inches(5.3), Inches(1.1))
        tf = tb.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = desc
        p.font.size = Pt(11)
        p.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 15: Conclusion, Testing & Future Scope
    # =========================================================================
    s15 = prs.slides.add_slide(blank_layout)
    add_header(s15, "Project Deliverables, Verification & Conclusion", "Summary & Next Steps")

    col_w = Inches(3.7)
    # Card 1: Deliverables
    add_card(s15, Inches(0.8), Inches(1.4), col_w, Inches(5.4), "Completed Deliverables", TEAL_PRIMARY)
    tb = s15.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(3.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    d_items = [
        "Production WAR: target/fitness-app.war built and ready for Tomcat deployment.",
        "Database Scripts: schema.sql (8 tables) and seed.sql (demo users and workouts).",
        "Git Repository: Exactly 30 chronological, meaningful commits.",
        "Comprehensive Documentation: Complete README.md and PROJECT_SPEC.md."
    ]
    for j, it in enumerate(d_items):
        p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
        p.text = f"✔ {it}"
        p.font.size = Pt(11)
        p.font.color.rgb = TEXT_DARK
        if j > 0: p.space_before = Pt(10)

    # Card 2: Test Results
    add_card(s15, Inches(4.8), Inches(1.4), col_w, Inches(5.4), "Automated Testing Verification", BLUE_ACCENT)
    tb = s15.shapes.add_textbox(Inches(5.0), Inches(2.0), Inches(3.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    t_items = [
        "23/23 Automated Unit & Integration Tests Passed (0 failures, 0 errors).",
        "In-Memory H2 DB: Fast, isolated test executions without external dependencies.",
        "Validation Testing: Email regex, password rules, boundaries, and XSS escaping.",
        "Domain Testing: BCrypt hashing consistency and biometric BMI calculations."
    ]
    for j, it in enumerate(t_items):
        p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
        p.text = f"✔ {it}"
        p.font.size = Pt(11)
        p.font.color.rgb = TEXT_DARK
        if j > 0: p.space_before = Pt(10)

    # Card 3: Future Scope
    add_card(s15, Inches(8.8), Inches(1.4), col_w, Inches(5.4), "Future Scope & Review Q&A", DARK_NAVY)
    tb = s15.shapes.add_textbox(Inches(9.0), Inches(2.0), Inches(3.3), Inches(4.5))
    tf = tb.text_frame
    tf.word_wrap = True
    f_items = [
        "Wearable Integration: Ingest heart rate and GPS routes from smartwatch BLE APIs.",
        "RESTful API Layer: Expose JSON endpoints for companion Android/iOS native apps.",
        "Social Leaderboards: Gamified fitness tiers (Bronze, Silver, Gold, Platinum).",
        "Open for Evaluator Review & Faculty Questions."
    ]
    for j, it in enumerate(f_items):
        p = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
        p.text = f"★ {it}"
        p.font.size = Pt(11)
        p.font.color.rgb = TEXT_DARK
        if j > 0: p.space_before = Pt(10)

    # Save
    out_path = "online-fitness-tracking-presentation.pptx"
    prs.save(out_path)
    print(f"Presentation successfully created at: {os.path.abspath(out_path)}")

if __name__ == "__main__":
    create_deck()
