<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

<div style="margin-bottom: 20px;">
    <h1 style="font-size: 22px;">Personalized Fitness Suggestions</h1>
    <p style="color: var(--text-secondary); font-size: 14px;">Algorithmic recommendations generated from your training volume, recovery spacing, and goal progress.</p>
</div>

<!-- Mandatory Non-Medical Advice Disclaimer Banner -->
<div class="disclaimer-banner">
    <strong>Health & Safety Notice:</strong> ${disclaimer}
</div>

<!-- Active Personalized Suggestions Grid -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; margin-bottom: 36px;">
    <c:forEach var="rec" items="${recommendations}">
        <div class="card" style="border-left: 4px solid ${rec.priority == 'HIGH' ? '#e11d48' : (rec.priority == 'MEDIUM' ? 'var(--primary)' : 'var(--accent)')};">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                <span class="badge ${rec.priority == 'HIGH' ? 'badge-extreme' : (rec.priority == 'MEDIUM' ? 'badge-medium' : 'badge-low')}">
                    ${rec.priority} Priority
                </span>
                <span style="font-size: 12px; font-weight: 500; color: var(--text-muted);">${rec.category}</span>
            </div>
            <h3 style="font-size: 16px; margin-bottom: 8px;">${rec.title}</h3>
            <p style="font-size: 13.5px; color: var(--text-secondary); line-height: 1.5;">
                ${rec.advice}
            </p>
        </div>
    </c:forEach>
</div>

<!-- Curated Educational Content & Fitness Guides -->
<div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
    <div>
        <h2 style="font-size: 18px;">Curated Fitness Guides & Articles</h2>
        <p style="color: var(--text-secondary); font-size: 13.5px;">Peer-reviewed training and recovery guides published by coaches.</p>
    </div>
    <div style="display: flex; gap: 8px; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/recommendations" class="btn btn-sm ${empty selectedCategory ? 'btn-primary' : 'btn-secondary'}">All</a>
        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/recommendations?category=${cat.name()}"
               class="btn btn-sm ${selectedCategory == cat.name() ? 'btn-primary' : 'btn-secondary'}">
                ${cat.displayName}
            </a>
        </c:forEach>
    </div>
</div>

<c:choose>
    <c:when test="${empty articles}">
        <div class="card empty-state">
            <p>No published articles in this category currently.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(340px, 1fr)); gap: 20px;">
            <c:forEach var="art" items="${articles}">
                <div class="card" style="display: flex; flex-direction: column; justify-content: space-between;">
                    <div>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                            <span class="badge badge-medium">${art.category.displayName}</span>
                            <span style="font-size: 12px; color: var(--text-muted);">${art.createdAt}</span>
                        </div>
                        <h3 style="font-size: 16px; margin-bottom: 10px;">${art.title}</h3>
                        <p style="font-size: 13.5px; color: var(--text-secondary); line-height: 1.6; white-space: pre-line;">
                            ${art.content}
                        </p>
                    </div>
                    <div style="border-top: 1px solid var(--border-color); padding-top: 10px; margin-top: 16px; font-size: 12.5px; color: var(--text-muted);">
                        Author: <strong>${art.authorName}</strong>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
