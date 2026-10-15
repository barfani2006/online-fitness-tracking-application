<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../../common/header.jsp"/>
<jsp:include page="../../common/admin_sidebar.jsp"/>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">Registered Accounts & Roles</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Total registered system users: <strong>${totalCount}</strong></p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/users?action=new" class="btn btn-primary">+ Create User</a>
</div>

<!-- Search & Filtering Card -->
<div class="card" style="margin-bottom: 24px;">
    <form action="${pageContext.request.contextPath}/admin/users" method="GET" style="display: grid; grid-template-columns: 2fr 1fr 1fr auto auto; gap: 12px; align-items: flex-end;">
        <div>
            <label class="form-label" for="query">Search</label>
            <input type="text" id="query" name="query" class="form-control" placeholder="Search by name or email..." value="${query}">
        </div>
        <div>
            <label class="form-label" for="role">Role</label>
            <select id="role" name="role" class="form-select">
                <option value="">All Roles</option>
                <c:forEach var="r" items="${roles}">
                    <option value="${r.name()}" ${r.name() == selectedRole ? 'selected' : ''}>${r.name()}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label class="form-label" for="status">Status</label>
            <select id="status" name="status" class="form-select">
                <option value="">All Statuses</option>
                <c:forEach var="s" items="${statuses}">
                    <option value="${s.name()}" ${s.name() == selectedStatus ? 'selected' : ''}>${s.name()}</option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-primary" style="padding: 9px 16px;">Filter</button>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary" style="padding: 9px 12px;">Reset</a>
    </form>
</div>

<!-- Users Table -->
<div class="card">
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>User</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Fitness Level</th>
                    <th>Joined</th>
                    <th style="text-align: right;">Administrative Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>#${u.id}</td>
                        <td>
                            <strong>${u.fullName}</strong><br>
                            <span style="font-size: 12.5px; color: var(--text-muted);">${u.email}</span>
                        </td>
                        <td>
                            <span class="badge ${u.admin ? 'badge-extreme' : 'badge-medium'}">${u.role}</span>
                        </td>
                        <td>
                            <span class="badge ${u.active ? 'badge-active' : 'badge-expired'}">${u.status}</span>
                        </td>
                        <td>${u.fitnessLevel.displayName}</td>
                        <td style="font-size: 13px; color: var(--text-secondary);">${u.createdAt}</td>
                        <td style="text-align: right; white-space: nowrap;">
                            <!-- Toggle Status Form -->
                            <form action="${pageContext.request.contextPath}/admin/users" method="POST" style="display: inline;" onsubmit="return confirmAction('Toggle status for this account?');">
                                <input type="hidden" name="action" value="toggle_status">
                                <input type="hidden" name="id" value="${u.id}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                <button type="submit" class="btn btn-secondary btn-sm" style="margin-right: 4px;">
                                    ${u.active ? 'Deactivate' : 'Activate'}
                                </button>
                            </form>

                            <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${u.id}" class="btn btn-secondary btn-sm" style="margin-right: 4px;">Edit</a>

                            <c:if test="${u.id != sessionScope.currentUser.id}">
                                <form action="${pageContext.request.contextPath}/admin/users" method="POST" style="display: inline;" onsubmit="return confirmAction('Permanently delete this user and all associated workouts and goals?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <c:if test="${totalPages > 1}">
        <div style="display: flex; justify-content: center; gap: 8px; margin-top: 24px;">
            <c:forEach var="p" begin="1" end="${totalPages}">
                <a href="${pageContext.request.contextPath}/admin/users?page=${p}&query=${query}&role=${selectedRole}&status=${selectedStatus}"
                   class="btn btn-sm ${p == currentPage ? 'btn-primary' : 'btn-secondary'}">
                    ${p}
                </a>
            </c:forEach>
        </div>
    </c:if>
</div>

<jsp:include page="../../common/footer.jsp"/>
