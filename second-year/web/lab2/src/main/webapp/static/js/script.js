document.addEventListener('DOMContentLoaded', function () {
    let selectedX = null;
    let selectedR = null;
    let selectedY = null;

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

    document.querySelectorAll('#r-buttons button').forEach(button => {
        button.addEventListener('click', function () {
            selectedR = this.value;
            document.querySelectorAll('#r-buttons button').forEach(btn => btn.removeAttribute('selected'));
            this.setAttribute('selected', 'true');
        });
    });

    document.getElementById('point-form').addEventListener('submit', function (event) {
        event.preventDefault();

        selectedY = document.getElementById('y').value;

        if (!selectedX || !selectedR) {
            document.getElementById('error-msg').textContent = "Выберите значение для X и R";
            return;
        }

        if (selectedY === '' || isNaN(selectedY) || selectedY < -5 || selectedY > 5) {
            document.getElementById('error-msg').textContent = "Введите корректное значение Y [-5; 5]";
            return;
        }

        document.getElementById('error-msg').textContent = '';

        const url = `controller?x=${encodeURIComponent(selectedX)}&y=${encodeURIComponent(selectedY)}&radius=${encodeURIComponent(selectedR)}`;
        let clientStartTime = new Date().toLocaleTimeString();
        const table = document.getElementById('result-table');
        const tbody = table ? table.querySelector('tbody') : null;
        const toggleButton = document.getElementById('toggle-table-button');

        if (!tbody) {
            console.error('Элемент <tbody> не найден');
            return;
        }

        fetch(url, {
            method: 'GET',
        })
            .then(response => response.text())
            .then(html => {
                if (table.style.display === 'none' || !table.style.display) {
                    table.style.display = 'table';
                    toggleButton.textContent = 'Скрыть таблицу';
                }

                let row = document.createElement('tr');
                let cells = html.match(/<td>(.*?)<\/td>/g).map(cell => cell.replace(/<\/?td>/g, ''));

                row.innerHTML = `
                    <td>${cells[0]}</td>
                    <td>${cells[1]}</td>
                    <td>${cells[2]}</td>
                    <td>${cells[3]}</td>
                    <td>${cells[4]}</td>
                    <td>${cells[5]}</td>`;

                tbody.insertAdjacentElement('afterbegin', row);

                let rows = tbody.querySelectorAll('tr');
                if (rows.length > 10) {
                    tbody.removeChild(rows[rows.length - 1]);
                }
            })
            .catch(error => {
                console.error('Ошибка при отправке запроса или обработке ответа:', error);

                if (table.style.display === 'none' || !table.style.display) {
                    table.style.display = 'table';
                    toggleButton.textContent = 'Скрыть таблицу';
                }

                let errorRow = document.createElement('tr');
                errorRow.innerHTML = `
                    <td>${selectedX}</td>
                    <td>${selectedY}</td>
                    <td>${selectedR}</td>
                    <td style="color: orange;">?</td>
                    <td>${clientStartTime}</td>
                    <td style="color: red;">Ошибка</td>`;

                errorRow.classList.add('new-row');
                tbody.insertAdjacentElement('afterbegin', errorRow);

                let rows = tbody.querySelectorAll('tr');
                if (rows.length > 10) {
                    tbody.removeChild(rows[rows.length - 1]);
                }
            });

        console.log('Отправлен запрос:', url);
    });
});
