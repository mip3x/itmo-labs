let selectedX = null;
let selectedR = null;
let selectedY = null;

document.querySelectorAll('#x-buttons button').forEach(button => {
    button.addEventListener('click', function() {
        selectedX = this.value;
        document.querySelectorAll('#x-buttons button').forEach(btn => btn.removeAttribute('selected'));
        this.setAttribute('selected', 'true');
    });
});

document.querySelectorAll('#r-buttons button').forEach(button => {
    button.addEventListener('click', function() {
        selectedR = this.value;
        document.querySelectorAll('#r-buttons button').forEach(btn => btn.removeAttribute('selected'));
        this.setAttribute('selected', 'true');
    });
});

document.getElementById('point-form').addEventListener('submit', function(event) {
    event.preventDefault();

    selectedY = document.getElementById('y').value;

    if (!selectedX || !selectedR) {
        document.getElementById('error-msg').textContent = "Выберите значения для X и R";
        return;
    }

    if (selectedY === '' || isNaN(selectedY) || selectedY < -3 || selectedY > 5) {
        document.getElementById('error-msg').textContent = "Введите корректное значение Y [-3; 5]";
        return;
    }

    document.getElementById('error-msg').textContent = '';

    let requestData = new URLSearchParams();
    requestData.append('x', selectedX);
    requestData.append('y', selectedY);
    requestData.append('r', selectedR);

    let clientStartTime = new Date().toLocaleTimeString();
    const table = document.getElementById('result-table');
    const toggleButton = document.getElementById('toggle-table-button');

    fetch('/fcgi-bin/server.jar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: requestData.toString()
    })
        .then(response => response.text())
        .then(html => {
            if (table.style.display === 'none' || !table.style.display) {
                table.style.display = 'table';
                toggleButton.textContent = 'Скрыть таблицу';
            }

            let tbody = document.querySelector('#result-table tbody');

            let row = document.createElement('tr');
            let cells = html.match(/<td>(.*?)<\/td>/g).map(cell => cell.replace(/<\/?td>/g, ''));

            row.innerHTML = `
                <td>${cells[0]}</td>
                <td>${cells[1]}</td>
                <td>${cells[2]}</td>
                <td>${cells[3] === 'true' ? '<span style="color: green;">&#10004;</span>' : '<span style="color: red;">&#10008;</span>'}</td>
                <td>${clientStartTime}</td>
                <td>${cells[4]} ms</td>`;

            tbody.insertAdjacentElement('afterbegin', row);

            let rows = tbody.querySelectorAll('tr');
            if (rows.length > 10) {
                tbody.removeChild(rows[rows.length - 1]);
            }

            // tbody.insertAdjacentHTML('afterbegin', html);
        })
        .catch(error => {
            console.error('Ошибка при отправке запроса:', error);

            if (table.style.display === 'none' || !table.style.display) {
                table.style.display = 'table';
                toggleButton.textContent = 'Скрыть таблицу';
            }

            let tbody = document.querySelector('#result-table tbody');
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

            tbody.insertAdjacentElement('afterbegin', errorRow);
        });

    console.log('Отправлен запрос:', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: requestData
    });
});