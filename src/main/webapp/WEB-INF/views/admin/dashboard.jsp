<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/admin_sidebar.jsp"/>

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 22px;">Platform Activity & Statistics</h1>
    <p style="color: var(--text-secondary); font-size: 14px;">High-level overview of system metrics, user growth, and pending administrative tasks.</p>
</div>

<!-- Platform KPI Cards -->
<div class="grid-cards">
    <div class="card stat-card" style="border-left: 4px solid #2563eb;">
        <span class="stat-label">Registered Users</span>
        <span class="stat-value">${stats.totalUsers}</span>
        <span class="stat-subtext">${stats.activeSessions} active session(s) right now</span>
    </div>

    <div class="card stat-card" style="border-left: 4px solid var(--primary);">
        <span class="stat-label">Workouts Logged</span>
        <span class="stat-value">${stats.totalWorkouts}</span>
        <span class="stat-subtext">${stats.totalMinutes} total active minutes</span>
    </div>

    <div class="card stat-card" style="border-left: 4px solid #f59e0b;">
        <span class="stat-label">Energy Expended</span>
        <span class="stat-value">${stats.totalCalories} <span style="font-size: 15px; font-weight: normal; color: var(--text-secondary);">kcal</span></span>
        <span class="stat-subtext">Across all member sessions</span>
    </div>

    <div class="card stat-card" style="border-left: 4px solid #16a34a;">
        <span class="stat-label">Goals Completed</span>
        <span class="stat-value">${stats.completedGoals}</span>
        <span class="stat-subtext">${stats.activeGoals} currently in progress</span>
    </div>
</div>

<c:if test="${stats.pendingContent > 0}">
    <div class="card" style="margin-bottom: 24px; border-left: 4px solid #e11d48; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
        <div>
            <strong style="color: #9f1239; font-size: 15px;">Pending Content Review Required</strong>
            <p style="font-size: 13.5px; color: var(--text-secondary); margin-top: 2px;">
                There are <strong>${stats.pendingContent}</strong> educational submission(s) awaiting approval before publication.
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/content?status=PENDING" class="btn btn-sm btn-primary" style="background: #e11d48;">Review Queue</a>
    </div>
</c:if>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
    <!-- Recent Users -->
    <div class="card">
        <div class="card-header">
            <div class="card-title">Recent Member Registrations</div>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary btn-sm">View All Users</a>
        </div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>User</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Joined</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${recentUsers}">
                        <tr>
                            <td>
                                <strong>${u.fullName}</strong><br>
                                <span style="font-size: 12px; color: var(--text-muted);">${u.email}</span>
                            </td>
                            <td><span class="badge ${u.admin ? 'badge-extreme' : 'badge-medium'}">${u.role}</span></td>
                            <td><span class="badge ${u.active ? 'badge-active' : 'badge-expired'}">${u.status}</span></td>
                            <td style="font-size: 12.5px; color: var(--text-secondary);">${u.createdAt}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Administrative Quick Links -->
    <div class="card">
        <div class="card-header">
            <div class="card-title">Quick Administration Actions</div>
        </div>
        <div style="display: flex; flex-direction: column; gap: 10px;">
            <a href="${pageContext.request.contextPath}/admin/users?action=new" class="btn btn-secondary btn-block" style="justify-content: flex-start;">
                + Create New User Account
            </a>
            <a href="${pageContext.request.contextPath}/admin/content?action=new" class="btn btn-secondary btn-block" style="justify-content: flex-start;">
                + Publish Fitness Guide
            </a>
            <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-secondary btn-block" style="justify-content: flex-start;">
                Manage System Settings
            </a>
            <a href="${pageContext.request.contextPath}/admin/activity" class="btn btn-secondary btn-block" style="justify-content: flex-start;">
                View Audit Activity Logs
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
