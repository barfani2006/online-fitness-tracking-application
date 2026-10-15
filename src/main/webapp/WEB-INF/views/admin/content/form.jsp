<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../../common/header.jsp"/>
<jsp:include page="../../common/admin_sidebar.jsp"/>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="card" style="max-width: 720px; margin: 0 auto;">
    <div class="card-header">
        <h2 style="font-size: 18px;">${isEdit ? "Edit Fitness Guide" : "Publish New Fitness Guide"}</h2>
        <a href="${pageContext.request.contextPath}/admin/content" class="btn btn-secondary btn-sm">Cancel</a>
    </div>

    <form action="${pageContext.request.contextPath}/admin/content" method="POST">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${article.id}">
        </c:if>

        <div class="form-group">
            <label for="title" class="form-label">Article Title *</label>
            <input type="text" id="title" name="title" class="form-control"
                   placeholder="e.g. Fundamental Principles of Progressive Overload"
                   value="${isEdit ? article.title : ''}" required>
        </div>

        <div class="form-group">
            <label for="category" class="form-label">Topic Category *</label>
            <select id="category" name="category" class="form-select" required>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.name()}" ${(isEdit && article.category == cat) ? 'selected' : ''}>
                        ${cat.displayName}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="content" class="form-label">Body Content *</label>
            <textarea id="content" name="content" class="form-control" style="min-height: 200px;"
                      placeholder="Write structured, accurate health and fitness recommendations..." required>${isEdit ? article.content : ''}</textarea>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/admin/content" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">${isEdit ? "Update Article" : "Publish Article"}</button>
        </div>
    </form>
</div>

<jsp:include page="../../common/footer.jsp"/>
