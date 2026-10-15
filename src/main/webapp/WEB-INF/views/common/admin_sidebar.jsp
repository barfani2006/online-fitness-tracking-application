<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="sidebar" style="background-color: #020617;">
    <div class="sidebar-header" style="border-bottom-color: #0f172a;">
        <span class="brand-badge" style="background: #e11d48;">Admin</span>
        <span class="brand-title">PulseFit Console</span>
    </div>

    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard"
           class="nav-link ${activeNav == 'admin-dashboard' ? 'active' : ''}">
            <span>Overview & Stats</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/users"
           class="nav-link ${activeNav == 'admin-users' ? 'active' : ''}">
            <span>User Management</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/content"
           class="nav-link ${activeNav == 'admin-content' ? 'active' : ''}">
            <span>Content Moderation</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/settings"
           class="nav-link ${activeNav == 'admin-settings' ? 'active' : ''}">
            <span>System Settings</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/activity"
           class="nav-link ${activeNav == 'admin-activity' ? 'active' : ''}">
            <span>Activity Monitoring</span>
        </a>

        <div style="margin: 16px 10px 4px 10px; font-size: 11px; text-transform: uppercase; color: #64748b; letter-spacing: 0.5px;">
            User Space
        </div>
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
            <span>User Dashboard</span>
        </a>
    </nav>

    <div class="sidebar-footer" style="border-top-color: #0f172a;">
        <div class="user-snippet">
            <span class="name">${sessionScope.currentUser.fullName}</span>
            <span class="email" style="color: #fda4af;">Administrator</span>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm btn-block" style="background: #0f172a; color: #e2e8f0; border-color: #1e293b;">
            Log Out
        </a>
    </div>
</aside>

<div class="main-wrapper">
    <header class="top-bar">
        <div class="top-bar-title">${not empty pageHeading ? pageHeading : "Administration Console"}</div>
        <div style="font-size: 13px; color: #e11d48; font-weight: 600;">
            Superuser Mode
        </div>
    </header>
    <div class="content-body">
