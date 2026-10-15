/**
 * PulseFit Application Script
 * Client-side enhancements for accessibility, form validation cues,
 * instant metric calculations, and interactive confirmations.
 */
document.addEventListener('DOMContentLoaded', function () {
    // 1. Accessible auto-dismiss for flash alerts (5 seconds)
    const alerts = document.querySelectorAll('.alert-dismissible, .alert');
    alerts.forEach(function (alert) {
        if (!alert.querySelector('.alert-close')) {
            const closeBtn = document.createElement('button');
            closeBtn.type = 'button';
            closeBtn.className = 'alert-close';
            closeBtn.setAttribute('aria-label', 'Close alert');
            closeBtn.innerHTML = '&times;';
            closeBtn.style.cssText = 'float: right; background: none; border: none; font-size: 18px; cursor: pointer; color: inherit; opacity: 0.7; margin-left: 10px; line-height: 1;';
            closeBtn.onclick = function () {
                alert.style.transition = 'opacity 0.3s ease, max-height 0.3s ease';
                alert.style.opacity = '0';
                setTimeout(function () { alert.remove(); }, 300);
            };
            alert.insertBefore(closeBtn, alert.firstChild);
        }
    });

    // 2. Interactive Workout Form Helper: Auto-estimate calories burned
    const durationInput = document.getElementById('durationMinutes');
    const typeSelect = document.getElementById('workoutType');
    const intensitySelect = document.getElementById('intensity');
    const caloriesInput = document.getElementById('caloriesBurned');

    if (durationInput && typeSelect && intensitySelect && caloriesInput) {
        function calculateEstimatedCalories() {
            // Only suggest if user hasn't typed a custom calorie value or if calorie is empty/0
            const duration = parseInt(durationInput.value, 10);
            if (isNaN(duration) || duration <= 0) return;

            // MET (Metabolic Equivalent of Task) table approximation
            const metValues = {
                'RUNNING': 9.8,
                'CYCLING': 7.5,
                'SWIMMING': 8.0,
                'WEIGHT_TRAINING': 5.0,
                'YOGA': 3.0,
                'HIIT': 11.0,
                'WALKING': 3.8,
                'PILATES': 4.0,
                'BOXING': 10.0,
                'OTHER': 5.0
            };

            const intensityMultipliers = {
                'LOW': 0.8,
                'MEDIUM': 1.0,
                'HIGH': 1.25,
                'EXTREME': 1.5
            };

            const type = typeSelect.value || 'OTHER';
            const intensity = intensitySelect.value || 'MEDIUM';

            const baseMet = metValues[type] || 5.0;
            const multiplier = intensityMultipliers[intensity] || 1.0;

            // Estimated calories = MET * weight(kg: default ~70) * (duration / 60) * multiplier
            const estimated = Math.round(baseMet * 70.0 * (duration / 60.0) * multiplier);

            if (!caloriesInput.value || caloriesInput.dataset.autoCalculated === 'true') {
                caloriesInput.value = estimated;
                caloriesInput.dataset.autoCalculated = 'true';
            }
        }

        durationInput.addEventListener('input', calculateEstimatedCalories);
        typeSelect.addEventListener('change', calculateEstimatedCalories);
        intensitySelect.addEventListener('change', calculateEstimatedCalories);

        caloriesInput.addEventListener('input', function () {
            // If user manually types something, mark as non-auto
            caloriesInput.dataset.autoCalculated = 'false';
        });
    }

    // 3. Client-side Form Validation Feedback
    const validatedForms = document.querySelectorAll('form.needs-validation');
    validatedForms.forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // 4. Client-side Quick Table Search (if data-table-filter attribute exists)
    const searchInputs = document.querySelectorAll('[data-table-filter]');
    searchInputs.forEach(function (searchInput) {
        const tableSelector = searchInput.getAttribute('data-table-filter');
        const targetTable = document.querySelector(tableSelector);
        if (targetTable) {
            searchInput.addEventListener('keyup', function () {
                const term = searchInput.value.toLowerCase();
                const rows = targetTable.querySelectorAll('tbody tr');
                rows.forEach(function (row) {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.indexOf(term) > -1 ? '' : 'none';
                });
            });
        }
    });
});

/**
 * Global accessible action confirmation
 */
function confirmAction(message) {
    return confirm(message || 'Are you sure you want to proceed with this action? This operation cannot be undone.');
}
