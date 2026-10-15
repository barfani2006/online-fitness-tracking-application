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

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h1 style="font-size: 22px;">Workout History</h1>
        <p style="color: var(--text-secondary); font-size: 14px;">Total records logged: <strong>${totalCount}</strong></p>
    </div>
    <a href="${pageContext.request.contextPath}/workouts?action=new" class="btn btn-primary">+ Log New Workout</a>
</div>

<!-- Search & Filtering Card -->
<div class="card" style="margin-bottom: 24px;">
    <form action="${pageContext.request.contextPath}/workouts" method="GET" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; align-items: flex-end;">
        <div>
            <label class="form-label" for="keyword">Keyword</label>
            <input type="text" id="keyword" name="keyword" class="form-control" placeholder="Search activity, notes..." value="${keyword}">
        </div>
        <div>
            <label class="form-label" for="categoryId">Category</label>
            <select id="categoryId" name="categoryId" class="form-select">
                <option value="">All Categories</option>
                <c:forEach var="c" items="${categories}">
                    <option value="${c.id}" ${c.id == selectedCategoryId ? 'selected' : ''}>${c.name}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label class="form-label" for="startDate">From Date</label>
            <input type="date" id="startDate" name="startDate" class="form-control" value="${startDate}">
        </div>
        <div>
            <label class="form-label" for="endDate">To Date</label>
            <input type="date" id="endDate" name="endDate" class="form-control" value="${endDate}">
        </div>
        <div>
            <label class="form-label" for="sortBy">Sort By</label>
            <select id="sortBy" name="sortBy" class="form-select">
                <option value="date" ${sortBy == 'date' ? 'selected' : ''}>Date</option>
                <option value="duration" ${sortBy == 'duration' ? 'selected' : ''}>Duration</option>
                <option value="calories" ${sortBy == 'calories' ? 'selected' : ''}>Calories</option>
                <option value="activity" ${sortBy == 'activity' ? 'selected' : ''}>Activity Name</option>
            </select>
        </div>
        <div style="display: flex; gap: 8px;">
            <button type="submit" class="btn btn-primary btn-block" style="padding: 9px;">Filter</button>
            <a href="${pageContext.request.contextPath}/workouts" class="btn btn-secondary" style="padding: 9px 12px;">Reset</a>
        </div>
    </form>
</div>

<!-- Workouts Table -->
<c:choose>
    <c:when test="${empty workouts}">
        <div class="card empty-state">
            <h3>No workouts matching query</h3>
            <p style="margin-bottom: 16px;">Try adjusting your search criteria or add a new workout record.</p>
            <a href="${pageContext.request.contextPath}/workouts?action=new" class="btn btn-primary btn-sm">+ Log New Workout</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Activity</th>
                        <th>Category</th>
                        <th>Duration</th>
                        <th>Intensity</th>
                        <th>Calories</th>
                        <th>Notes</th>
                        <th style="text-align: right;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="w" items="${workouts}">
                        <tr>
                            <td><strong>${w.workoutDate}</strong></td>
                            <td>${w.activityName}</td>
                            <td><span style="color: var(--text-secondary);">${not empty w.categoryName ? w.categoryName : 'General'}</span></td>
                            <td>${w.durationMinutes} min</td>
                            <td>
                                <span class="badge badge-${w.intensity.name().toLowerCase()}">${w.intensity.displayName}</span>
                            </td>
                            <td>${w.caloriesBurned} kcal</td>
                            <td style="max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 13px; color: var(--text-secondary);">
                                ${not empty w.notes ? w.notes : '-'}
                            </td>
                            <td style="text-align: right; white-space: nowrap;">
                                <a href="${pageContext.request.contextPath}/workouts?action=edit&id=${w.id}" class="btn btn-secondary btn-sm" style="margin-right: 4px;">Edit</a>
                                <form action="${pageContext.request.contextPath}/workouts" method="POST" style="display: inline;" onsubmit="return confirmAction('Are you sure you want to delete this workout log?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${w.id}">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                </form>
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
                    <a href="${pageContext.request.contextPath}/workouts?page=${p}&keyword=${keyword}&categoryId=${selectedCategoryId}&startDate=${startDate}&endDate=${endDate}&sortBy=${sortBy}&sortOrder=${sortOrder}"
                       class="btn btn-sm ${p == currentPage ? 'btn-primary' : 'btn-secondary'}">
                        ${p}
                    </a>
                </c:forEach>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp"/>
