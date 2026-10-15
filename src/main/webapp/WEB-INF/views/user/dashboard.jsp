<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>

<!-- Quick Action Header -->
<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">Hello, ${sessionScope.currentUser.fullName}</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Here is your current fitness overview and recent performance.</p>
    </div>
    <div style="display: flex; gap: 10px;">
        <a href="${pageContext.request.contextPath}/workouts?action=new" class="btn btn-primary">+ Log Workout</a>
        <a href="${pageContext.request.contextPath}/goals?action=new" class="btn btn-secondary">+ New Goal</a>
    </div>
</div>

<!-- KPI Cards -->
<div class="grid-cards">
    <div class="card stat-card">
        <span class="stat-label">Total Workouts</span>
        <span class="stat-value">${not empty stats.totalCount ? stats.totalCount : 0}</span>
        <span class="stat-subtext">${not empty stats.thisWeekCount ? stats.thisWeekCount : 0} logged past 7 days</span>
    </div>
    <div class="card stat-card">
        <span class="stat-label">Total Active Time</span>
        <span class="stat-value">${not empty stats.totalMinutes ? stats.totalMinutes : 0} <span style="font-size: 16px; font-weight: normal; color: var(--text-secondary);">min</span></span>
        <span class="stat-subtext">${not empty stats.thisWeekMinutes ? stats.thisWeekMinutes : 0} min this week</span>
    </div>
    <div class="card stat-card">
        <span class="stat-label">Calories Burned</span>
        <span class="stat-value">${not empty stats.totalCalories ? stats.totalCalories : 0} <span style="font-size: 16px; font-weight: normal; color: var(--text-secondary);">kcal</span></span>
        <span class="stat-subtext">${not empty stats.thisWeekCalories ? stats.thisWeekCalories : 0} kcal this week</span>
    </div>
    <div class="card stat-card">
        <span class="stat-label">Average Session</span>
        <span class="stat-value">${not empty stats.avgDuration ? stats.avgDuration : 0} <span style="font-size: 16px; font-weight: normal; color: var(--text-secondary);">min</span></span>
        <span class="stat-subtext">Level: <strong>${sessionScope.currentUser.fitnessLevel}</strong></span>
    </div>
</div>

<!-- Recommendation Alert Box -->
<c:if test="${not empty recommendations}">
    <div class="card" style="margin-bottom: 24px; border-left: 4px solid var(--primary);">
        <div class="card-header" style="margin-bottom: 10px;">
            <div class="card-title">Personalized Training Suggestions</div>
            <a href="${pageContext.request.contextPath}/recommendations" style="font-size: 13px; font-weight: 500;">View All</a>
        </div>
        <div style="font-size: 13px; color: var(--text-secondary); margin-bottom: 12px; line-height: 1.5;">
            <strong>${recommendations[0].title}:</strong> ${recommendations[0].advice}
        </div>
        <div style="font-size: 11.5px; color: #b45309; background: #fef3c7; padding: 6px 10px; border-radius: var(--radius-sm);">
            ${recommendations[0].disclaimer}
        </div>
    </div>
</c:if>

<!-- Charts & Main Content Layout -->
<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px; margin-bottom: 24px;">
    <!-- Recent Workouts -->
    <div class="card">
        <div class="card-header">
            <div class="card-title">Recent Workouts</div>
            <a href="${pageContext.request.contextPath}/workouts" class="btn btn-secondary btn-sm">View All</a>
        </div>
        <c:choose>
            <c:when test="${empty recentWorkouts}">
                <div class="empty-state">
                    <h3>No workouts recorded yet</h3>
                    <p style="margin-bottom: 16px;">Log your first session to initiate automated tracking and analytics.</p>
                    <a href="${pageContext.request.contextPath}/workouts?action=new" class="btn btn-primary btn-sm">Log First Workout</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Activity</th>
                                <th>Category</th>
                                <th>Duration</th>
                                <th>Calories</th>
                                <th>Intensity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="w" items="${recentWorkouts}">
                                <tr>
                                    <td>${w.workoutDate}</td>
                                    <td><strong>${w.activityName}</strong></td>
                                    <td>${not empty w.categoryName ? w.categoryName : 'General'}</td>
                                    <td>${w.durationMinutes} min</td>
                                    <td>${w.caloriesBurned} kcal</td>
                                    <td>
                                        <span class="badge badge-${w.intensity.name().toLowerCase()}">${w.intensity.displayName}</span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Active Goals & Challenges Side Panel -->
    <div style="display: flex; flex-direction: column; gap: 24px;">
        <!-- Active Goals -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">Active Goals</div>
                <a href="${pageContext.request.contextPath}/goals" style="font-size: 12.5px;">Manage</a>
            </div>
            <c:choose>
                <c:when test="${empty activeGoals}">
                    <div style="font-size: 13.5px; color: var(--text-secondary); text-align: center; padding: 16px 0;">
                        No active goals.<br>
                        <a href="${pageContext.request.contextPath}/goals?action=new" style="font-size: 13px;">+ Set a target</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="g" items="${activeGoals}">
                        <div style="margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px solid var(--border-color);">
                            <div style="display: flex; justify-content: space-between; font-size: 13.5px; font-weight: 500;">
                                <span>${g.title}</span>
                                <span>${g.currentValue} / ${g.targetValue} ${g.unit}</span>
                            </div>
                            <div class="progress-container">
                                <div class="progress-bar" style="width: ${g.progressPercentage}%;"></div>
                            </div>
                            <div style="font-size: 11.5px; color: var(--text-muted); display: flex; justify-content: space-between;">
                                <span>${g.progressPercentage}% Completed</span>
                                <span>Due: ${g.targetDate}</span>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Active Challenges -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">Enrolled Challenges</div>
                <a href="${pageContext.request.contextPath}/challenges" style="font-size: 12.5px;">Browse</a>
            </div>
            <c:choose>
                <c:when test="${empty activeChallenges}">
                    <div style="font-size: 13.5px; color: var(--text-secondary); text-align: center; padding: 16px 0;">
                        Not enrolled in any challenges.<br>
                        <a href="${pageContext.request.contextPath}/challenges" style="font-size: 13px;">Explore Challenges</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="c" items="${activeChallenges}">
                        <div style="margin-bottom: 12px;">
                            <div style="font-size: 13.5px; font-weight: 600;">${c.challengeTitle}</div>
                            <div style="font-size: 12px; color: var(--text-secondary); margin-bottom: 4px;">
                                Badge: <strong>${c.badgeName}</strong> (${c.currentProgress} / ${c.targetGoal} ${c.targetMetric.unit})
                            </div>
                            <div class="progress-container">
                                <div class="progress-bar" style="width: ${c.progressPercentage}%; background-color: var(--accent);"></div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
