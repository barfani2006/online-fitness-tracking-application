<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="card" style="max-width: 680px; margin: 0 auto;">
    <div class="card-header">
        <h2 style="font-size: 18px;">${isEdit ? "Edit Workout Record" : "Record New Workout"}</h2>
        <a href="${pageContext.request.contextPath}/workouts" class="btn btn-secondary btn-sm">Cancel</a>
    </div>

    <form action="${pageContext.request.contextPath}/workouts" method="POST">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${workout.id}">
        </c:if>

        <div class="form-group">
            <label for="activityName" class="form-label">Activity / Exercise Name *</label>
            <input type="text" id="activityName" name="activityName" class="form-control"
                   placeholder="e.g. Morning 5K Jog, Upper Body Hypertrophy"
                   value="${isEdit ? workout.activityName : ''}" required>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="categoryId" class="form-label">Category</label>
                <select id="categoryId" name="categoryId" class="form-select">
                    <option value="">Select Category</option>
                    <c:forEach var="c" items="${categories}">
                        <option value="${c.id}" ${(isEdit && workout.categoryId == c.id) ? 'selected' : ''}>
                            ${c.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="workoutDate" class="form-label">Date *</label>
                <input type="date" id="workoutDate" name="workoutDate" class="form-control"
                       value="${isEdit ? workout.workoutDate : todayDate}" required>
            </div>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="durationMinutes" class="form-label">Duration (Minutes) *</label>
                <input type="number" id="durationMinutes" name="durationMinutes" class="form-control"
                       min="1" max="720" placeholder="e.g. 45"
                       value="${isEdit ? workout.durationMinutes : ''}" required>
            </div>
            <div class="form-group">
                <label for="intensity" class="form-label">Intensity Level *</label>
                <select id="intensity" name="intensity" class="form-select" required>
                    <c:forEach var="inten" items="${intensities}">
                        <option value="${inten.name()}" ${(isEdit && workout.intensity == inten) ? 'selected' : (inten.name() == 'MEDIUM' ? 'selected' : '')}>
                            ${inten.displayName}
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="caloriesBurned" class="form-label">Calories Burned (kcal) <span style="font-weight: normal; color: var(--text-muted);">(Leave blank to auto-calculate)</span></label>
            <input type="number" id="caloriesBurned" name="caloriesBurned" class="form-control"
                   min="0" max="10000" placeholder="Auto-calculated if blank"
                   value="${isEdit ? workout.caloriesBurned : ''}">
        </div>

        <div class="form-group">
            <label for="notes" class="form-label">Workout Notes & Reflections</label>
            <textarea id="notes" name="notes" class="form-control"
                      placeholder="Felt energetic, increased weights on bench press, heart rate stats...">${isEdit ? workout.notes : ''}</textarea>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/workouts" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">${isEdit ? "Save Changes" : "Log Workout"}</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
