<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="card" style="max-width: 600px; margin: 0 auto;">
    <div class="card-header">
        <h2 style="font-size: 18px;">${isEdit ? "Edit Fitness Goal" : "Set New Fitness Goal"}</h2>
        <a href="${pageContext.request.contextPath}/goals" class="btn btn-secondary btn-sm">Cancel</a>
    </div>

    <form action="${pageContext.request.contextPath}/goals" method="POST">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${goal.id}">
        </c:if>

        <div class="form-group">
            <label for="title" class="form-label">Goal Title *</label>
            <input type="text" id="title" name="title" class="form-control"
                   placeholder="e.g. Complete 20 Workouts This Month, Reach 75kg Bodyweight"
                   value="${isEdit ? goal.title : ''}" required>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="goalType" class="form-label">Target Metric Type *</label>
                <select id="goalType" name="goalType" class="form-select" ${isEdit ? 'disabled' : ''} required>
                    <c:forEach var="gt" items="${goalTypes}">
                        <option value="${gt.name()}" ${(isEdit && goal.goalType == gt) ? 'selected' : ''}>
                            ${gt.displayName} (${gt.defaultUnit})
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${isEdit}">
                    <input type="hidden" name="goalType" value="${goal.goalType.name()}">
                </c:if>
            </div>

            <div class="form-group">
                <label for="targetValue" class="form-label">Target Value *</label>
                <input type="number" step="0.1" id="targetValue" name="targetValue" class="form-control"
                       placeholder="e.g. 20" value="${isEdit ? goal.targetValue : ''}" required>
            </div>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="unit" class="form-label">Unit of Measure</label>
                <input type="text" id="unit" name="unit" class="form-control"
                       placeholder="e.g. workouts, min, kcal, kg"
                       value="${isEdit ? goal.unit : ''}">
            </div>

            <div class="form-group">
                <label for="targetDate" class="form-label">Target Completion Date *</label>
                <input type="date" id="targetDate" name="targetDate" class="form-control"
                       value="${isEdit ? goal.targetDate : ''}" required>
            </div>
        </div>

        <c:if test="${isEdit}">
            <div class="form-group">
                <label for="status" class="form-label">Status</label>
                <select id="status" name="status" class="form-select">
                    <option value="ACTIVE" ${goal.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                    <option value="COMPLETED" ${goal.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                    <option value="EXPIRED" ${goal.status == 'EXPIRED' ? 'selected' : ''}>Expired</option>
                </select>
            </div>
        </c:if>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/goals" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">${isEdit ? "Update Goal" : "Save Goal"}</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
