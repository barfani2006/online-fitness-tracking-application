<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Application Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="auth-wrapper">
    <div class="auth-card" style="text-align: center;">
        <span class="brand-badge" style="background: var(--danger); font-size: 14px;">Server Error</span>
        <h1 style="margin: 16px 0 8px 0; font-size: 26px;">500 Internal Error</h1>
        <p style="color: var(--text-secondary); margin-bottom: 24px;">
            An unexpected error occurred while processing your request. The technical details have been logged for administrative review.
        </p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</body>
</html>
