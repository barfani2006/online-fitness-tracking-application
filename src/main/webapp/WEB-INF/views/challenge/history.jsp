<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">My Challenge History</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Historical record of all community challenges you joined and achievements unlocked.</p>
    </div>
    <a href="${pageContext.request.contextPath}/challenges" class="btn btn-secondary">← Back to Challenge Browser</a>
</div>

<c:choose>
    <c:when test="${empty history}">
        <div class="card empty-state">
            <h3>No challenge participation records</h3>
            <p style="margin-bottom: 16px;">You haven't joined any challenges yet.</p>
            <a href="${pageContext.request.contextPath}/challenges" class="btn btn-primary btn-sm">Explore Active Challenges</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Challenge</th>
                            <th>Target Metric</th>
                            <th>Your Progress</th>
                            <th>Status</th>
                            <th>Badge Award</th>
                            <th>Enrolled Date</th>
                            <th>Completed Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${history}">
                            <tr>
                                <td><strong>${h.challengeTitle}</strong></td>
                                <td>${h.targetMetric.displayName}</td>
                                <td>
                                    <div style="font-size: 13px;">${h.currentProgress} / ${h.targetGoal} ${h.targetMetric.unit} (${h.progressPercentage}%)</div>
                                    <div class="progress-container" style="width: 120px;">
                                        <div class="progress-bar" style="width: ${h.progressPercentage}%; background-color: ${h.status == 'COMPLETED' ? '#16a34a' : 'var(--primary)'};"></div>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge ${h.status == 'COMPLETED' ? 'badge-completed' : (h.status == 'IN_PROGRESS' ? 'badge-active' : 'badge-expired')}">
                                        ${h.status}
                                    </span>
                                </td>
                                <td>
                                    <span style="font-weight: 500; color: #b45309;">${h.badgeName}</span>
                                </td>
                                <td>${h.joinedAt}</td>
                                <td>${not empty h.completedAt ? h.completedAt : '-'}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
