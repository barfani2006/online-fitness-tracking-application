<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 22px;">Profile & Biometric Settings</h1>
    <p style="color: var(--text-secondary); font-size: 14px;">Manage your account credentials, body metrics, and training level.</p>
</div>

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">

    <!-- Left Column: Personal Information Form -->
    <div class="card">
        <div class="card-header">
            <div class="card-title">Personal Details & Body Metrics</div>
        </div>

        <form action="${pageContext.request.contextPath}/profile" method="POST">
            <input type="hidden" name="action" value="update_profile">
            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

            <div class="form-group">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control" value="${user.fullName}" required>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Email Address (Read-Only)</label>
                <input type="email" id="email" class="form-control" value="${user.email}" disabled style="background-color: #f1f5f9; cursor: not-allowed;">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="age" class="form-label">Age</label>
                    <input type="number" id="age" name="age" class="form-control" min="10" max="120" value="${user.age}">
                </div>
                <div class="form-group">
                    <label for="gender" class="form-label">Gender</label>
                    <select id="gender" name="gender" class="form-select">
                        <c:forEach var="g" items="${genders}">
                            <option value="${g.name()}" ${user.gender == g ? 'selected' : ''}>${g.name()}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="weightKg" class="form-label">Weight (kg)</label>
                    <input type="number" step="0.1" id="weightKg" name="weightKg" class="form-control" value="${user.weightKg}">
                </div>
                <div class="form-group">
                    <label for="heightCm" class="form-label">Height (cm)</label>
                    <input type="number" step="0.1" id="heightCm" name="heightCm" class="form-control" value="${user.heightCm}">
                </div>
            </div>

            <div class="form-group">
                <label for="fitnessLevel" class="form-label">Fitness Level</label>
                <select id="fitnessLevel" name="fitnessLevel" class="form-select">
                    <c:forEach var="lvl" items="${fitnessLevels}">
                        <option value="${lvl.name()}" ${user.fitnessLevel == lvl ? 'selected' : ''}>
                            ${lvl.displayName} - ${lvl.description}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Profile Changes</button>
            </div>
        </form>
    </div>

    <!-- Right Column: Biometric Overview & Password Change -->
    <div style="display: flex; flex-direction: column; gap: 24px;">

        <!-- Calculated BMI Overview -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">Biometric Insights</div>
            </div>
            <c:choose>
                <c:when test="${not empty bmi}">
                    <div style="display: flex; align-items: baseline; gap: 12px; margin-bottom: 8px;">
                        <span style="font-size: 32px; font-weight: 700; color: var(--primary);">
                            <fmt:formatNumber value="${bmi}" maxFractionDigits="1"/>
                        </span>
                        <span style="font-size: 14px; color: var(--text-secondary);">
                            <c:choose>
                                <c:when test="${bmi < 18.5}"><strong style="color: #3b82f6;">Underweight</strong></c:when>
                                <c:when test="${bmi <= 24.9}"><strong style="color: #16a34a;">Normal Weight</strong></c:when>
                                <c:when test="${bmi <= 29.9}"><strong style="color: #d97706;">Overweight</strong></c:when>
                                <c:otherwise><strong style="color: #e11d48;">Obese</strong></c:otherwise>
                            </c:choose>
                            (BMI Category)
                        </span>
                    </div>
                    <p style="font-size: 12.5px; color: var(--text-muted); line-height: 1.4;">
                        Body Mass Index (BMI) is a screening metric based on height and weight. Individual muscle composition can vary.
                    </p>
                </c:when>
                <c:otherwise>
                    <div class="empty-state" style="padding: 10px 0;">
                        <p>Enter your weight and height on the left to calculate your BMI.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Password Change Card -->
        <div class="card">
            <div class="card-header">
                <div class="card-title">Security & Password</div>
            </div>
            <form action="${pageContext.request.contextPath}/profile" method="POST">
                <input type="hidden" name="action" value="change_password">
                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                <div class="form-group">
                    <label for="currentPassword" class="form-label">Current Password *</label>
                    <input type="password" id="currentPassword" name="currentPassword" class="form-control" required>
                </div>

                <div class="form-group">
                    <label for="newPassword" class="form-label">New Password * (Min 8 chars, letters & digits)</label>
                    <input type="password" id="newPassword" name="newPassword" class="form-control" required>
                </div>

                <div class="form-group">
                    <label for="confirmNewPassword" class="form-label">Confirm New Password *</label>
                    <input type="password" id="confirmNewPassword" name="confirmNewPassword" class="form-control" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-secondary">Update Password</button>
                </div>
            </form>
        </div>

    </div>

</div>

<jsp:include page="../common/footer.jsp"/>
