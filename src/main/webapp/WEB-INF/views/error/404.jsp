<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="auth-wrapper">
    <div class="auth-card" style="text-align: center;">
        <span class="brand-badge" style="background: var(--warning); font-size: 14px;">Resource Missing</span>
        <h1 style="margin: 16px 0 8px 0; font-size: 26px;">404 Page Not Found</h1>
        <p style="color: var(--text-secondary); margin-bottom: 24px;">
            The page or record you requested could not be located on the server.
        </p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</body>
</html>
