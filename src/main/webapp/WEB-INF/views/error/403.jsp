<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Access Forbidden</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="auth-wrapper">
    <div class="auth-card" style="text-align: center;">
        <span class="brand-badge" style="background: var(--danger); font-size: 14px;">Access Denied</span>
        <h1 style="margin: 16px 0 8px 0; font-size: 26px;">403 Forbidden</h1>
        <p style="color: var(--text-secondary); margin-bottom: 24px;">
            ${not empty errorMessage ? errorMessage : "You do not have administrative permission to view this resource."}
        </p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</body>
</html>
