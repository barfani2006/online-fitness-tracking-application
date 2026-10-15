<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/admin_sidebar.jsp"/>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="card" style="max-width: 620px; margin: 0 auto;">
    <div class="card-header">
        <h2 style="font-size: 18px;">${isEdit ? "Edit User Account" : "Create New User Account"}</h2>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary btn-sm">Cancel</a>
    </div>

    <form action="${pageContext.request.contextPath}/admin/users" method="POST">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${targetUser.id}">
        </c:if>

        <div class="form-group">
            <label for="fullName" class="form-label">Full Name *</label>
            <input type="text" id="fullName" name="fullName" class="form-control"
                   value="${isEdit ? targetUser.fullName : ''}" required>
        </div>

        <div class="form-group">
            <label for="email" class="form-label">Email Address *</label>
            <input type="email" id="email" name="email" class="form-control"
                   value="${isEdit ? targetUser.email : ''}" required>
        </div>

        <c:if test="${!isEdit}">
            <div class="form-group">
                <label for="password" class="form-label">Temporary Password *</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="At least 8 chars" required>
            </div>
        </c:if>

        <div class="form-row">
            <div class="form-group">
                <label for="role" class="form-label">Access Role *</label>
                <select id="role" name="role" class="form-select" required>
                    <c:forEach var="r" items="${roles}">
                        <option value="${r.name()}" ${(isEdit && targetUser.role == r) ? 'selected' : ''}>${r.name()}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="status" class="form-label">Account Status *</label>
                <select id="status" name="status" class="form-select" required>
                    <c:forEach var="s" items="${statuses}">
                        <option value="${s.name()}" ${(isEdit && targetUser.status == s) ? 'selected' : ''}>${s.name()}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">${isEdit ? "Update User" : "Create Account"}</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
