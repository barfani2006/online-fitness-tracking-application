<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="sidebar">
    <div class="sidebar-header">
        <span class="brand-badge">PulseFit</span>
        <span class="brand-title">Fitness Tracker</span>
    </div>

    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/dashboard"
           class="nav-link ${activeNav == 'dashboard' ? 'active' : ''}">
            <span>Dashboard</span>
        </a>
        <a href="${pageContext.request.contextPath}/workouts"
           class="nav-link ${activeNav == 'workouts' ? 'active' : ''}">
            <span>My Workouts</span>
        </a>
        <a href="${pageContext.request.contextPath}/workouts/progress"
           class="nav-link ${activeNav == 'progress' ? 'active' : ''}">
            <span>Progress Analytics</span>
        </a>
        <a href="${pageContext.request.contextPath}/goals"
           class="nav-link ${activeNav == 'goals' ? 'active' : ''}">
            <span>Fitness Goals</span>
        </a>
        <a href="${pageContext.request.contextPath}/challenges"
           class="nav-link ${activeNav == 'challenges' ? 'active' : ''}">
            <span>Challenges</span>
        </a>
        <a href="${pageContext.request.contextPath}/recommendations"
           class="nav-link ${activeNav == 'recommendations' ? 'active' : ''}">
            <span>Recommendations</span>
        </a>
        <a href="${pageContext.request.contextPath}/profile"
           class="nav-link ${activeNav == 'profile' ? 'active' : ''}">
            <span>Profile & Health</span>
        </a>
        <c:if test="${sessionScope.currentUser.admin}">
            <div style="margin: 12px 10px 4px 10px; font-size: 11px; text-transform: uppercase; color: #64748b; letter-spacing: 0.5px;">
                Administration
            </div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">
                <span>Switch to Admin</span>
            </a>
        </c:if>
    </nav>

    <div class="sidebar-footer">
        <div class="user-snippet">
            <span class="name">${sessionScope.currentUser.fullName}</span>
            <span class="email">${sessionScope.currentUser.email}</span>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm btn-block" style="background: #1e293b; color: #e2e8f0; border-color: #334155;">
            Log Out
        </a>
    </div>
</aside>

<div class="main-wrapper">
    <header class="top-bar">
        <div class="top-bar-title">${not empty pageHeading ? pageHeading : "Dashboard"}</div>
        <div style="font-size: 13.5px; color: var(--text-secondary);">
            Active User: <strong>${sessionScope.currentUser.fullName}</strong>
        </div>
    </header>
    <div class="content-body">
