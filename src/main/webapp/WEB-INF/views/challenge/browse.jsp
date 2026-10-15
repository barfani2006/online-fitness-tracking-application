<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">Community Fitness Challenges</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Join structured fitness challenges to stay motivated, earn achievement badges, and track your streaks.</p>
    </div>
    <a href="${pageContext.request.contextPath}/challenges?view=history" class="btn btn-secondary">View My Challenge History</a>
</div>

<c:choose>
    <c:when test="${empty challenges}">
        <div class="card empty-state">
            <h3>No active challenges currently</h3>
            <p>New challenges will be posted by the fitness community administrators shortly.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(340px, 1fr)); gap: 24px;">
            <c:forEach var="ch" items="${challenges}">
                <div class="card" style="display: flex; flex-direction: column; justify-content: space-between; border-top: 4px solid ${ch.userJoined ? 'var(--primary)' : 'var(--border-color)'};">
                    <div>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                            <span class="badge ${ch.active ? 'badge-active' : 'badge-expired'}">
                                ${ch.active ? 'Active Challenge' : 'Past Challenge'}
                            </span>
                            <span style="font-size: 12.5px; color: var(--text-muted);">${ch.durationDays} Days Duration</span>
                        </div>

                        <h3 style="font-size: 17px; margin-bottom: 6px;">${ch.title}</h3>
                        <p style="font-size: 13.5px; color: var(--text-secondary); margin-bottom: 14px; line-height: 1.5;">
                            ${ch.description}
                        </p>

                        <div style="background: #f8fafc; border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 12px; margin-bottom: 16px;">
                            <div style="font-size: 13px; margin-bottom: 4px;">
                                Target: <strong>${ch.targetGoal} ${ch.targetMetric.displayName}</strong>
                            </div>
                            <div style="font-size: 12.5px; color: #b45309;">
                                Award: <strong>${ch.badgeName}</strong>
                            </div>
                            <div style="font-size: 12px; color: var(--text-muted); margin-top: 6px;">
                                Window: ${ch.startDate} to ${ch.endDate} • ${ch.participantCount} participant(s)
                            </div>
                        </div>

                        <c:if test="${ch.userJoined}">
                            <div style="margin-bottom: 16px;">
                                <div style="display: flex; justify-content: space-between; font-size: 13px; font-weight: 500; margin-bottom: 4px;">
                                    <span>Your Progress</span>
                                    <span>${ch.userProgress} / ${ch.targetGoal} (${Math.round((ch.userProgress * 100.0) / ch.targetGoal)}%)</span>
                                </div>
                                <div class="progress-container">
                                    <div class="progress-bar" style="width: ${Math.min(100, Math.round((ch.userProgress * 100.0) / ch.targetGoal))}%; background-color: ${ch.userStatus == 'COMPLETED' ? '#16a34a' : 'var(--primary)'};"></div>
                                </div>
                                <c:if test="${ch.userStatus == 'COMPLETED'}">
                                    <span style="font-size: 12px; color: #16a34a; font-weight: 600;">Challenge Completed! Badge Earned</span>
                                </c:if>
                            </div>
                        </c:if>
                    </div>

                    <!-- Enrollment Button -->
                    <div style="border-top: 1px solid var(--border-color); padding-top: 14px;">
                        <c:choose>
                            <c:when test="${ch.userJoined}">
                                <form action="${pageContext.request.contextPath}/challenges" method="POST" onsubmit="return confirmAction('Leave this challenge? You will no longer track progress towards it.');">
                                    <input type="hidden" name="action" value="leave">
                                    <input type="hidden" name="challengeId" value="${ch.id}">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-secondary btn-sm btn-block" style="color: var(--danger);">
                                        Leave Challenge
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/challenges" method="POST">
                                    <input type="hidden" name="action" value="join">
                                    <input type="hidden" name="challengeId" value="${ch.id}">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-primary btn-block" ${!ch.active ? 'disabled' : ''}>
                                        ${ch.active ? 'Join Challenge' : 'Challenge Closed'}
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
