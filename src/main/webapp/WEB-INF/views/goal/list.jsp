<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">Personal Fitness Goals</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Define quantitative targets and track automated or manual milestone completion.</p>
    </div>
    <a href="${pageContext.request.contextPath}/goals?action=new" class="btn btn-primary">+ Create New Goal</a>
</div>

<c:choose>
    <c:when test="${empty goals}">
        <div class="card empty-state">
            <h3>No goals defined yet</h3>
            <p style="margin-bottom: 16px;">Setting a target for weekly workouts, duration, or weight helps maintain accountability.</p>
            <a href="${pageContext.request.contextPath}/goals?action=new" class="btn btn-primary btn-sm">+ Set Your First Goal</a>
        </div>
    </c:when>
    <c:otherwise>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px;">
            <c:forEach var="g" items="${goals}">
                <div class="card" style="display: flex; flex-direction: column; justify-content: space-between;">
                    <div>
                        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 10px;">
                            <span class="badge ${g.status == 'COMPLETED' ? 'badge-completed' : (g.status == 'EXPIRED' ? 'badge-expired' : 'badge-active')}">
                                ${g.status}
                            </span>
                            <span style="font-size: 12px; color: var(--text-muted);">Due: ${g.targetDate}</span>
                        </div>
                        <h3 style="font-size: 16px; margin-bottom: 6px;">${g.title}</h3>
                        <div style="font-size: 13px; color: var(--text-secondary); margin-bottom: 14px;">
                            Type: <strong>${g.goalType.displayName}</strong>
                        </div>

                        <!-- Progress Bar -->
                        <div style="display: flex; justify-content: space-between; font-size: 13.5px; margin-bottom: 4px;">
                            <span>Current: <strong>${g.currentValue}</strong> ${g.unit}</span>
                            <span>Target: <strong>${g.targetValue}</strong> ${g.unit}</span>
                        </div>
                        <div class="progress-container">
                            <div class="progress-bar" style="width: ${g.progressPercentage}%; background-color: ${g.status == 'COMPLETED' ? '#16a34a' : 'var(--primary)'};"></div>
                        </div>
                        <div style="font-size: 12px; color: var(--text-muted); text-align: right; margin-bottom: 16px;">
                            ${g.progressPercentage}% Completed
                        </div>
                    </div>

                    <!-- Quick Update & Actions -->
                    <div style="border-top: 1px solid var(--border-color); padding-top: 14px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px;">
                        <form action="${pageContext.request.contextPath}/goals" method="POST" style="display: flex; gap: 6px; align-items: center;">
                            <input type="hidden" name="action" value="update_progress">
                            <input type="hidden" name="id" value="${g.id}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                            <input type="number" step="0.1" name="currentValue" value="${g.currentValue}" class="form-control" style="width: 80px; padding: 4px 8px; font-size: 13px;" required>
                            <button type="submit" class="btn btn-secondary btn-sm" title="Quickly update current progress">Log</button>
                        </form>

                        <div style="display: flex; gap: 6px;">
                            <a href="${pageContext.request.contextPath}/goals?action=edit&id=${g.id}" class="btn btn-secondary btn-sm">Edit</a>
                            <form action="${pageContext.request.contextPath}/goals" method="POST" style="display: inline;" onsubmit="return confirmAction('Delete this goal permanently?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${g.id}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp"/>
