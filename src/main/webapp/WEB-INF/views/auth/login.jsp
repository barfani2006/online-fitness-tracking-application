<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - PulseFit Tracker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="auth-wrapper">

    <div class="auth-card">
        <div class="auth-header">
            <span class="brand-badge" style="font-size: 16px; padding: 6px 12px;">PulseFit</span>
            <h1 style="margin-top: 12px;">Welcome Back</h1>
            <p>Sign in to record your workouts and view progress</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success" role="alert">
                ${successMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="POST" novalidate>
            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group">
                <label for="email" class="form-label">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="e.g. john@fitness.com" value="${formEmail}" required autofocus>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="padding: 10px; margin-top: 8px;">
                Sign In
            </button>
        </form>

        <div style="margin-top: 20px; text-align: center; font-size: 13.5px; color: var(--text-secondary);">
            Don't have an account? <a href="${pageContext.request.contextPath}/register">Create an account</a>
        </div>

        <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--border-color); font-size: 12px; color: var(--text-muted);">
            <strong>Demo Credentials:</strong><br>
            • Admin: <code>admin@fitness.com</code> / <code>Admin@123</code><br>
            • User: <code>john@fitness.com</code> / <code>User@123</code>
        </div>
    </div>

</body>
</html>
