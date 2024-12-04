document.addEventListener('DOMContentLoaded', function () {
    let selectedX = null;
    let selectedRadius = localStorage.getItem('selectedRadius') ? parseInt(localStorage.getItem('selectedRadius')) : null;
    let selectedY = null;

    if (selectedRadius) {
        document.getElementById('radius').value = selectedRadius; // Устанавливаем радиус в скрытое поле
        document.querySelectorAll('#r-buttons button').forEach((button) => {
            if (parseInt(button.value) === selectedRadius) {
                button.setAttribute('selected', 'true');
                button.style.backgroundColor = '#fbfbfb';
            }
        });
    }

    document.querySelectorAll('#x-checkboxes input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            if (this.checked) {
                document.querySelectorAll('#x-checkboxes input[type="checkbox"]').forEach(cb => {
                    if (cb !== this) cb.checked = false;
                });
                selectedX = this.value;
            } else {
                selectedX = null;
            }
        });
    });

    document.querySelectorAll('#r-buttons button').forEach((button) => {
        button.addEventListener('click', function () {
            selectedRadius = parseInt(this.value);
            document.getElementById('radius').value = selectedRadius;
            localStorage.setItem('selectedRadius', selectedRadius);

            document.querySelectorAll('#r-buttons button').forEach((btn) => {
                btn.removeAttribute('selected');
                btn.style.backgroundColor = '';
            });

            this.setAttribute('selected', 'true');
            this.style.backgroundColor = '#fbfbfb';
        });
    });

    document.getElementById('point-form').addEventListener('submit', function (event) {
        selectedY = document.getElementById('y').value;

        if (!selectedX || !selectedRadius) {
            document.getElementById('error-msg').textContent = "Выберите значение для X и R";
            event.preventDefault();
            return;
        }

        if (selectedY === '' || isNaN(selectedY) || selectedY < -5 || selectedY > 5) {
            document.getElementById('error-msg').textContent = "Введите корректное значение Y [-5; 5]";
            event.preventDefault();
            return;
        }

        document.getElementById('error-msg').textContent = '';
    });
});
