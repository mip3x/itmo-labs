document.addEventListener('DOMContentLoaded', function () {
    let selectedX = null;
    let selectedR = window.selectedRadius;
    if (selectedR === 100) selectedR = null;
    let selectedY = null;

    // Сбрасываем выбор чекбоксов
    document.querySelectorAll('#x-checkboxes input[type="checkbox"]').forEach(checkbox => {
        checkbox.checked = false;
    });

    // Сбрасываем кнопки для радиуса
    document.querySelectorAll('#r-buttons button').forEach(button => {
        button.removeAttribute('selected');
    });

    // Сбрасываем поле для Y
    document.getElementById('y').value = '';

    // Обработка выбора X
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

    // Обработка выбора R
    document.querySelectorAll('#r-buttons button').forEach(button => {
        button.addEventListener('click', function () {
            selectedR = this.value;
            document.querySelectorAll('#r-buttons button').forEach(btn => btn.removeAttribute('selected'));
            this.setAttribute('selected', 'true');
        });
    });

    // Обработка отправки формы
    document.getElementById('point-form').addEventListener('submit', function (event) {
        event.preventDefault();

        selectedY = document.getElementById('y').value;
        console.log(selectedR);

        if (!selectedX || !selectedR) {
            document.getElementById('error-msg').textContent = "Выберите значение для X и R";
            return;
        }

        if (selectedY === '' || isNaN(selectedY) || selectedY < -5 || selectedY > 5) {
            document.getElementById('error-msg').textContent = "Введите корректное значение Y [-5; 5]";
            return;
        }

        document.getElementById('error-msg').textContent = '';

        const url = `checkData?x=${encodeURIComponent(selectedX)}&y=${encodeURIComponent(selectedY)}&radius=${encodeURIComponent(selectedR)}`;
        const table = document.getElementById('result-table');
        const tbody = table.querySelector('tbody');
        const toggleButton = document.getElementById('toggle-table-button');

        // Отправляем AJAX-запрос
        fetch(url, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) throw new Error("Ошибка при отправке запроса");
                return response.text();
            })
            .then(html => {
                // Делаем таблицу видимой
                if (table.style.display === 'none' || !table.style.display) {
                    table.style.display = 'table';
                    toggleButton.textContent = 'Скрыть таблицу';
                }

                // Добавляем новую строку в таблицу
                const newRow = document.createElement('tr');
                newRow.innerHTML = html.trim();
                tbody.insertBefore(newRow, tbody.firstChild);

                // Извлекаем данные для отрисовки точки
                const cells = newRow.querySelectorAll('td');
                const result = {
                    x: parseFloat(cells[0].textContent),
                    y: parseFloat(cells[1].textContent),
                    radius: parseInt(cells[2].textContent),
                    hit: cells[3].innerHTML.includes('green')
                };

                // Рисуем точку
                // drawPoint(result.x, result.y, result.radius, parseInt(selectedR), result.hit);
                drawPoint(result.x, result.y, result.radius);

                // Ограничиваем количество строк в таблице до 10
                if (tbody.rows.length > 10) {
                    tbody.deleteRow(tbody.rows.length - 1);
                }
            })
            .catch(error => {
                console.error('Ошибка при отправке запроса или обработке ответа:', error);

                if (table.style.display === 'none' || !table.style.display) {
                    table.style.display = 'table';
                    toggleButton.textContent = 'Скрыть таблицу';
                }

                // Добавляем строку с ошибкой
                const errorRow = document.createElement('tr');
                errorRow.innerHTML = `
                    <td>${selectedX}</td>
                    <td>${selectedY}</td>
                    <td>${selectedR}</td>
                    <td style="color: orange;">?</td>
                    <td>${new Date().toLocaleTimeString()}</td>
                    <td style="color: red;">Ошибка</td>`;
                tbody.insertBefore(errorRow, tbody.firstChild);

                if (tbody.rows.length > 10) {
                    tbody.deleteRow(tbody.rows.length - 1);
                }
            });
    });
});
