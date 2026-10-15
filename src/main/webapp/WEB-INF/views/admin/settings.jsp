<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/admin_sidebar.jsp"/>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 22px;">Application Configuration Settings</h1>
    <p style="color: var(--text-secondary); font-size: 14px;">Modify operational parameters, registration policies, and recommendation rules without restarting the server.</p>
</div>

<div class="card" style="max-width: 760px;">
    <form action="${pageContext.request.contextPath}/admin/settings" method="POST">
        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

        <c:forEach var="s" items="${settings}">
            <div class="form-group" style="padding-bottom: 16px; border-bottom: 1px solid var(--border-color);">
                <label for="${s.key}" class="form-label">
                    <code>${s.key}</code>
                </label>
                <div style="font-size: 12.5px; color: var(--text-muted); margin-bottom: 6px;">
                    ${s.description}
                </div>
                <c:choose>
                    <c:when test="${s.value == 'true' || s.value == 'false'}">
                        <select id="${s.key}" name="${s.key}" class="form-select" style="max-width: 200px;">
                            <option value="true" ${s.value == 'true' ? 'selected' : ''}>Enabled (true)</option>
                            <option value="false" ${s.value == 'false' ? 'selected' : ''}>Disabled (false)</option>
                        </select>
                    </c:when>
                    <c:otherwise>
                        <input type="text" id="${s.key}" name="${s.key}" class="form-control" value="${s.value}" required>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:forEach>

        <div class="form-actions" style="margin-top: 20px;">
            <button type="submit" class="btn btn-primary">Save Settings</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
