<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - PulseFit Tracker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="auth-wrapper">

    <div class="auth-card" style="max-width: 520px;">
        <div class="auth-header">
            <span class="brand-badge" style="font-size: 16px; padding: 6px 12px;">PulseFit</span>
            <h1 style="margin-top: 12px;">Create Account</h1>
            <p>Start recording your workouts and achieving your fitness goals</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${errorMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="POST">
            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group">
                <label for="fullName" class="form-label">Full Name *</label>
                <input type="text" id="fullName" name="fullName" class="form-control"
                       placeholder="e.g. Alex Morgan" value="${formFullName}" required>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Email Address *</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="e.g. alex@example.com" value="${formEmail}" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="password" class="form-label">Password *</label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="At least 8 chars" required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword" class="form-label">Confirm Password *</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                           placeholder="Repeat password" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="age" class="form-label">Age (Years)</label>
                    <input type="number" id="age" name="age" class="form-control" min="10" max="120" placeholder="e.g. 25">
                </div>
                <div class="form-group">
                    <label for="gender" class="form-label">Gender</label>
                    <select id="gender" name="gender" class="form-select">
                        <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="weightKg" class="form-label">Weight (kg)</label>
                    <input type="number" step="0.1" id="weightKg" name="weightKg" class="form-control" placeholder="e.g. 70.5">
                </div>
                <div class="form-group">
                    <label for="heightCm" class="form-label">Height (cm)</label>
                    <input type="number" step="0.1" id="heightCm" name="heightCm" class="form-control" placeholder="e.g. 175">
                </div>
            </div>

            <div class="form-group">
                <label for="fitnessLevel" class="form-label">Current Fitness Level</label>
                <select id="fitnessLevel" name="fitnessLevel" class="form-select">
                    <option value="BEGINNER">Beginner (Starting out)</option>
                    <option value="INTERMEDIATE">Intermediate (3-4 days/week)</option>
                    <option value="ADVANCED">Advanced (5+ days/week)</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="padding: 10px; margin-top: 8px;">
                Complete Registration
            </button>
        </form>

        <div style="margin-top: 20px; text-align: center; font-size: 13.5px; color: var(--text-secondary);">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
        </div>
    </div>

</body>
</html>
