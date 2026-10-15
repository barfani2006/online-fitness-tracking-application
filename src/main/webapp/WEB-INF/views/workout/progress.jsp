<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 22px;">Progress Analytics & Performance Trends</h1>
    <p style="color: var(--text-secondary); font-size: 14px;">Historical metrics and distribution derived from your logged workouts.</p>
</div>

<!-- KPI Cards -->
<div class="grid-cards">
    <div class="card stat-card">
        <span class="stat-label">Past 7 Days Time</span>
        <span class="stat-value">${progress.thisWeekMinutes} <span style="font-size: 16px; font-weight: normal; color: var(--text-secondary);">min</span></span>
        <span class="stat-subtext">${progress.thisWeekCount} sessions completed</span>
    </div>
    <div class="card stat-card">
        <span class="stat-label">Past 7 Days Calories</span>
        <span class="stat-value">${progress.thisWeekCalories} <span style="font-size: 16px; font-weight: normal; color: var(--text-secondary);">kcal</span></span>
        <span class="stat-subtext">Active energy expended</span>
    </div>
    <div class="card stat-card">
        <span class="stat-label">Consistency Score</span>
        <span class="stat-value">${progress.consistencyScore}%</span>
        <span class="stat-subtext">Target: 3-4 sessions/week</span>
    </div>
    <div class="card stat-card">
        <span class="stat-label">All-Time Average</span>
        <span class="stat-value">${progress.averageDurationMinutes} <span style="font-size: 16px; font-weight: normal; color: var(--text-secondary);">min</span></span>
        <span class="stat-subtext">Per recorded workout</span>
    </div>
</div>

<!-- Charts Row -->
<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px; margin-bottom: 24px;">
    <!-- Daily Volume Trend Chart -->
    <div class="card">
        <div class="card-header">
            <div class="card-title">Daily Active Minutes (Past 28 Days)</div>
        </div>
        <div style="position: relative; height: 280px; width: 100%;">
            <canvas id="dailyVolumeChart"></canvas>
        </div>
    </div>

    <!-- Intensity Breakdown Doughnut Chart -->
    <div class="card">
        <div class="card-header">
            <div class="card-title">Intensity Distribution</div>
        </div>
        <div style="position: relative; height: 280px; width: 100%;">
            <canvas id="intensityDoughnutChart"></canvas>
        </div>
    </div>
</div>

<!-- Category Distribution Table & Progress Breakdown -->
<div class="card">
    <div class="card-header">
        <div class="card-title">Category Breakdown</div>
    </div>
    <c:choose>
        <c:when test="${empty progress.categoryDistribution}">
            <div class="empty-state">
                <p>No categorized workout history found.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Category</th>
                            <th>Workouts Logged</th>
                            <th>Total Duration (min)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cat" items="${progress.categoryDistribution}">
                            <tr>
                                <td><strong>${cat.category}</strong></td>
                                <td>${cat.count} sessions</td>
                                <td>${cat.minutes} minutes</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Render Charts using Chart.js -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const dailyTrends = ${dailyTrendsJson};
        const intensityDist = ${intensityDistJson};

        // 1. Daily Volume Bar/Line Chart
        if (dailyTrends && dailyTrends.length > 0) {
            const labels = dailyTrends.map(d => d.date);
            const minutesData = dailyTrends.map(d => d.minutes);
            const caloriesData = dailyTrends.map(d => d.calories);

            const ctxVolume = document.getElementById('dailyVolumeChart').getContext('2d');
            new Chart(ctxVolume, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'Duration (min)',
                            data: minutesData,
                            backgroundColor: '#0d9488',
                            borderRadius: 4,
                            yAxisID: 'y'
                        },
                        {
                            label: 'Calories (kcal)',
                            data: caloriesData,
                            type: 'line',
                            borderColor: '#e11d48',
                            backgroundColor: '#e11d48',
                            tension: 0.2,
                            yAxisID: 'y1'
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            type: 'linear',
                            display: true,
                            position: 'left',
                            title: { display: true, text: 'Minutes' }
                        },
                        y1: {
                            type: 'linear',
                            display: true,
                            position: 'right',
                            grid: { drawOnChartArea: false },
                            title: { display: true, text: 'Calories (kcal)' }
                        }
                    }
                }
            });
        } else {
            const chartDiv = document.getElementById('dailyVolumeChart').parentElement;
            chartDiv.innerHTML = '<div class="empty-state"><p>No activity logged within the past 28 days to graph.</p></div>';
        }

        // 2. Intensity Distribution Doughnut Chart
        if (intensityDist && intensityDist.length > 0) {
            const labels = intensityDist.map(i => i.intensity);
            const counts = intensityDist.map(i => i.count);
            const colors = {
                'LOW': '#10b981',
                'MEDIUM': '#3b82f6',
                'HIGH': '#f59e0b',
                'EXTREME': '#ef4444'
            };
            const bgColors = labels.map(l => colors[l] || '#94a3b8');

            const ctxIntensity = document.getElementById('intensityDoughnutChart').getContext('2d');
            new Chart(ctxIntensity, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: counts,
                        backgroundColor: bgColors
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        } else {
            const chartDiv = document.getElementById('intensityDoughnutChart').parentElement;
            chartDiv.innerHTML = '<div class="empty-state"><p>No intensity records to display.</p></div>';
        }
    });
</script>

<jsp:include page="../common/footer.jsp"/>
