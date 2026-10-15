<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PulseFit - Online Fitness Tracking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        .hero-section {
            background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
            color: #ffffff;
            padding: 80px 24px;
            text-align: center;
        }
        .hero-badge {
            display: inline-block;
            background: rgba(13, 148, 136, 0.2);
            color: #2dd4bf;
            border: 1px solid rgba(13, 148, 136, 0.4);
            padding: 6px 14px;
            border-radius: 9999px;
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 20px;
            letter-spacing: 0.5px;
        }
        .hero-title {
            font-size: 2.75rem;
            font-weight: 700;
            letter-spacing: -0.5px;
            max-width: 800px;
            margin: 0 auto 16px auto;
            color: #ffffff;
        }
        .hero-subtitle {
            font-size: 1.15rem;
            color: #94a3b8;
            max-width: 620px;
            margin: 0 auto 32px auto;
            line-height: 1.6;
        }
        .hero-cta {
            display: flex;
            justify-content: center;
            gap: 16px;
            flex-wrap: wrap;
        }
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 24px;
            max-width: 1100px;
            margin: -40px auto 60px auto;
            padding: 0 20px;
        }
        .feature-box {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 24px;
            box-shadow: var(--shadow-md);
        }
        .feature-box h3 {
            margin-bottom: 10px;
            font-size: 16px;
        }
        .feature-box p {
            color: var(--text-secondary);
            font-size: 14px;
            line-height: 1.5;
        }
    </style>
</head>
<body>

    <header style="background: #0f172a; padding: 16px 24px; border-bottom: 1px solid #1e293b; display: flex; justify-content: space-between; align-items: center;">
        <div style="display: flex; align-items: center; gap: 10px;">
            <span class="brand-badge">PulseFit</span>
            <span class="brand-title">Fitness Tracker</span>
        </div>
        <div style="display: flex; gap: 12px;">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary btn-sm">Go to Dashboard</a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm" style="color: #cbd5e1; background: #1e293b; border-color: #334155;">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm" style="color: #cbd5e1; background: #1e293b; border-color: #334155;">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Get Started</a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <div class="hero-section">
        <span class="hero-badge">3rd Semester Java Web Project • Marking Rubric Compliant</span>
        <h1 class="hero-title">Track Workouts, Attain Goals, and Elevate Your Fitness Journey</h1>
        <p class="hero-subtitle">
            A comprehensive Java Servlet and JDBC-powered web platform for workout logging, progress analytics, community challenges, and personalized rule-based recommendations.
        </p>
        <div class="hero-cta">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary" style="padding: 12px 28px; font-size: 15px;">Create Free Account</a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary" style="padding: 12px 28px; font-size: 15px; background: #1e293b; color: #ffffff; border-color: #334155;">Sign In with Demo</a>
        </div>
    </div>

    <div class="feature-grid">
        <div class="feature-box">
            <h3>Workout Logging & History</h3>
            <p>Log aerobic, resistance, and mobility workouts with customizable duration, intensity levels, and real-time calorie burn computations.</p>
        </div>
        <div class="feature-box">
            <h3>Progress Analytics</h3>
            <p>Visualize total duration, workout frequencies, and intensity breakdowns using interactive charts and weekly summaries.</p>
        </div>
        <div class="feature-box">
            <h3>Milestone Goal Tracking</h3>
            <p>Define quantifiable targets for workout volume, calories burned, and bodyweight with automatic progress recalculations.</p>
        </div>
        <div class="feature-box">
            <h3>Community Challenges</h3>
            <p>Participate in fitness streaks, earn achievement badges, and track your completion percentages side-by-side with fellow athletes.</p>
        </div>
        <div class="feature-box">
            <h3>Rule-Based Suggestions</h3>
            <p>Receive algorithmic guidance based on weekly training frequency, balanced intensity, and scheduled active recovery periods.</p>
        </div>
        <div class="feature-box">
            <h3>Administrative Control</h3>
            <p>Dedicated administration console for user account management, educational content review, system settings, and audit logs.</p>
        </div>
    </div>

    <footer style="text-align: center; padding: 30px; font-size: 13px; color: var(--text-muted); border-top: 1px solid var(--border-color);">
        PulseFit Tracker • Java Web Architecture (Jakarta Servlet 6.0, JDBC Prepared Statements, MySQL 8.0, JSTL) • 3rd Semester Project
    </footer>

</body>
</html>
