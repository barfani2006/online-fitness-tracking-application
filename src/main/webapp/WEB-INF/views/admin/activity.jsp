<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/admin_sidebar.jsp" />

<div class="page-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h1 style="font-size: 24px; font-weight: 700; color: #0f172a; margin: 0 0 6px 0;">System Activity & Audit Trail</h1>
        <p style="color: #64748b; font-size: 14px; margin: 0;">
            Real-time audit log of security events, administrative updates, and user modifications recorded asynchronously.
        </p>
    </div>
    <div style="background: #f1f5f9; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; color: #334155;">
        Total Logged Events: <span style="color: #e11d48;">${totalCount}</span>
    </div>
</div>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger" style="margin-bottom: 20px;">
        ${errorMessage}
    </div>
</c:if>

<!-- Filters Card -->
<div class="card" style="margin-bottom: 24px; padding: 16px;">
    <form action="${pageContext.request.contextPath}/admin/activity" method="GET" style="display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap;">
        <div class="form-group" style="margin-bottom: 0; min-width: 240px;">
            <label for="action" style="font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; color: #64748b; margin-bottom: 4px; display: block;">Filter by Event Action</label>
            <select id="action" name="action" class="form-control" style="height: 38px;">
                <option value="">All Actions</option>
                <option value="LOGIN_SUCCESS" ${selectedAction == 'LOGIN_SUCCESS' ? 'selected' : ''}>LOGIN_SUCCESS</option>
                <option value="LOGIN_FAILURE" ${selectedAction == 'LOGIN_FAILURE' ? 'selected' : ''}>LOGIN_FAILURE</option>
                <option value="USER_REGISTER" ${selectedAction == 'USER_REGISTER' ? 'selected' : ''}>USER_REGISTER</option>
                <option value="USER_CREATED" ${selectedAction == 'USER_CREATED' ? 'selected' : ''}>USER_CREATED</option>
                <option value="USER_UPDATED" ${selectedAction == 'USER_UPDATED' ? 'selected' : ''}>USER_UPDATED</option>
                <option value="USER_STATUS_CHANGED" ${selectedAction == 'USER_STATUS_CHANGED' ? 'selected' : ''}>USER_STATUS_CHANGED</option>
                <option value="USER_DELETED" ${selectedAction == 'USER_DELETED' ? 'selected' : ''}>USER_DELETED</option>
                <option value="CONTENT_APPROVED" ${selectedAction == 'CONTENT_APPROVED' ? 'selected' : ''}>CONTENT_APPROVED</option>
                <option value="CONTENT_REJECTED" ${selectedAction == 'CONTENT_REJECTED' ? 'selected' : ''}>CONTENT_REJECTED</option>
                <option value="SETTINGS_UPDATED" ${selectedAction == 'SETTINGS_UPDATED' ? 'selected' : ''}>SETTINGS_UPDATED</option>
            </select>
        </div>
        <div>
            <button type="submit" class="btn btn-primary" style="height: 38px; padding: 0 16px;">Filter Events</button>
            <c:if test="${not empty selectedAction}">
                <a href="${pageContext.request.contextPath}/admin/activity" class="btn btn-secondary" style="height: 38px; padding: 0 16px; margin-left: 8px; line-height: 38px;">Reset</a>
            </c:if>
        </div>
    </form>
</div>

<!-- Audit Log Table -->
<div class="card" style="padding: 0; overflow: hidden;">
    <div style="padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center;">
        <h2 style="font-size: 16px; font-weight: 600; margin: 0; color: #1e293b;">Recent Activity Events</h2>
        <span style="font-size: 12px; color: #64748b;">Showing most recent 50 entries</span>
    </div>

    <c:choose>
        <c:when test="${empty logs}">
            <div style="padding: 48px 24px; text-align: center; color: #64748b;">
                <p style="font-size: 16px; font-weight: 500; margin-bottom: 8px;">No audit records found.</p>
                <p style="font-size: 13px; margin: 0;">Try adjusting your action filter or performing actions across the platform.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table" style="margin: 0; font-size: 13px;">
                    <thead style="background: #f8fafc; font-size: 11px; text-transform: uppercase; color: #64748b; letter-spacing: 0.5px;">
                        <tr>
                            <th style="padding: 12px 16px; width: 60px;">#ID</th>
                            <th style="padding: 12px 16px; width: 170px;">Timestamp</th>
                            <th style="padding: 12px 16px; width: 180px;">Action</th>
                            <th style="padding: 12px 16px; width: 180px;">Target User / Performed By</th>
                            <th style="padding: 12px 16px; width: 140px;">Entity</th>
                            <th style="padding: 12px 16px;">Details</th>
                            <th style="padding: 12px 16px; width: 120px;">IP Address</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${logs}">
                            <tr style="border-bottom: 1px solid #f1f5f9;">
                                <td style="padding: 12px 16px; color: #94a3b8; font-family: monospace;">#${item.id}</td>
                                <td style="padding: 12px 16px; color: #475569; white-space: nowrap;">
                                    <fmt:formatDate value="${item.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                </td>
                                <td style="padding: 12px 16px;">
                                    <c:choose>
                                        <c:when test="${item.action.contains('LOGIN_SUCCESS') or item.action.contains('APPROVED')}">
                                            <span class="badge" style="background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; font-weight: 600;">
                                                ${item.action}
                                            </span>
                                        </c:when>
                                        <c:when test="${item.action.contains('FAILURE') or item.action.contains('REJECTED') or item.action.contains('DELETED')}">
                                            <span class="badge" style="background: #fff1f2; color: #be123c; border: 1px solid #fecdd3; font-weight: 600;">
                                                ${item.action}
                                            </span>
                                        </c:when>
                                        <c:when test="${item.action.contains('UPDATED') or item.action.contains('CHANGED')}">
                                            <span class="badge" style="background: #eff6ff; color: #1d4ed8; border: 1px solid #bfdbfe; font-weight: 600;">
                                                ${item.action}
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge" style="background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; font-weight: 600;">
                                                ${item.action}
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="padding: 12px 16px; color: #1e293b; font-weight: 500;">
                                    <c:choose>
                                        <c:when test="${not empty item.userEmail}">
                                            <span>${item.userEmail}</span>
                                            <span style="font-size: 11px; color: #94a3b8; display: block;">ID: ${item.userId}</span>
                                        </c:when>
                                        <c:when test="${not empty item.userId}">
                                            User #${item.userId}
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #94a3b8; font-style: italic;">Anonymous / System</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="padding: 12px 16px; color: #475569;">
                                    <c:if test="${not empty item.entityType}">
                                        <span style="font-weight: 500;">${item.entityType}</span>
                                        <c:if test="${not empty item.entityId}">
                                            <span style="color: #94a3b8;">#${item.entityId}</span>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${empty item.entityType}">
                                        <span style="color: #94a3b8;">-</span>
                                    </c:if>
                                </td>
                                <td style="padding: 12px 16px; color: #334155; word-break: break-word;">
                                    ${item.details}
                                </td>
                                <td style="padding: 12px 16px; color: #64748b; font-family: monospace; font-size: 12px;">
                                    ${not empty item.ipAddress ? item.ipAddress : "127.0.0.1"}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
