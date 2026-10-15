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

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">Fitness Content Moderation</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Review submissions, moderate community tips, and publish educational wellness guides.</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/content?action=new" class="btn btn-primary">+ Publish Guide</a>
</div>

<!-- Moderation Status Filters -->
<div style="display: flex; gap: 10px; margin-bottom: 20px; flex-wrap: wrap;">
    <a href="${pageContext.request.contextPath}/admin/content?status=ALL"
       class="btn btn-sm ${selectedStatus == 'ALL' ? 'btn-primary' : 'btn-secondary'}">
        All Articles (${contentList.size()})
    </a>
    <a href="${pageContext.request.contextPath}/admin/content?status=PENDING"
       class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-primary' : 'btn-secondary'}">
        Pending Review (${pendingCount})
    </a>
    <a href="${pageContext.request.contextPath}/admin/content?status=APPROVED"
       class="btn btn-sm ${selectedStatus == 'APPROVED' ? 'btn-primary' : 'btn-secondary'}">
        Approved (${approvedCount})
    </a>
    <a href="${pageContext.request.contextPath}/admin/content?status=REJECTED"
       class="btn btn-sm ${selectedStatus == 'REJECTED' ? 'btn-primary' : 'btn-secondary'}">
        Rejected
    </a>
</div>

<!-- Articles List -->
<c:choose>
    <c:when test="${empty contentList}">
        <div class="card empty-state">
            <h3>No articles matching filter</h3>
            <p>Select a different filter or publish a new article.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div style="display: flex; flex-direction: column; gap: 18px;">
            <c:forEach var="art" items="${contentList}">
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; flex-wrap: wrap; gap: 8px;">
                        <div>
                            <span class="badge ${art.status == 'APPROVED' ? 'badge-active' : (art.status == 'PENDING' ? 'badge-high' : 'badge-extreme')}">
                                ${art.status}
                            </span>
                            <span class="badge badge-medium" style="margin-left: 6px;">${art.category.displayName}</span>
                        </div>
                        <span style="font-size: 12.5px; color: var(--text-muted);">${art.createdAt}</span>
                    </div>

                    <h3 style="font-size: 17px; margin-bottom: 8px;">${art.title}</h3>
                    <p style="font-size: 14px; color: var(--text-secondary); line-height: 1.5; margin-bottom: 14px; white-space: pre-line;">
                        ${art.content}
                    </p>

                    <c:if test="${art.status == 'REJECTED' && not empty art.rejectionReason}">
                        <div style="font-size: 13px; color: #991b1b; background: #fee2e2; padding: 8px 12px; border-radius: var(--radius-sm); margin-bottom: 14px;">
                            <strong>Rejection Note:</strong> ${art.rejectionReason}
                        </div>
                    </c:if>

                    <div style="border-top: 1px solid var(--border-color); padding-top: 12px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px;">
                        <span style="font-size: 13px; color: var(--text-muted);">Author: <strong>${art.authorName}</strong></span>

                        <div style="display: flex; gap: 8px;">
                            <c:if test="${art.status != 'APPROVED'}">
                                <form action="${pageContext.request.contextPath}/admin/content" method="POST" style="display: inline;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="id" value="${art.id}">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-primary btn-sm" style="background: #16a34a;">Approve</button>
                                </form>
                            </c:if>

                            <c:if test="${art.status != 'REJECTED'}">
                                <form action="${pageContext.request.contextPath}/admin/content" method="POST" style="display: inline;" onsubmit="return promptRejectReason(this);">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="id" value="${art.id}">
                                    <input type="hidden" name="rejectionReason" value="">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-secondary btn-sm" style="color: var(--danger);">Reject</button>
                                </form>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/admin/content?action=edit&id=${art.id}" class="btn btn-secondary btn-sm">Edit</a>

                            <form action="${pageContext.request.contextPath}/admin/content" method="POST" style="display: inline;" onsubmit="return confirmAction('Delete this content permanently?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${art.id}">
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

<script>
    function promptRejectReason(form) {
        const reason = prompt("Enter the reason for rejection (optional):", "Content does not meet health guidelines or standards.");
        if (reason === null) return false;
        form.elements["rejectionReason"].value = reason;
        return true;
    }
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
