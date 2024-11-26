document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('graph');
    const context = canvas.getContext('2d');
    // let selectedRadius = 100;

    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;

    const axisLength = canvas.width;
    const scaleConst = 130;

    let selectedRadius = localStorage.getItem('selectedRadius') ? parseInt(localStorage.getItem('selectedRadius')) : 100;
    console.log("draw-graph selectedRadius: ", selectedRadius);
    window.selectedRadius = selectedRadius;

    const MAX_POINTS = 10;
    const results = [];
    if (savedResults && savedResults.trim().length > 0) {
        const rows = savedResults.split('</tr>');
        rows.forEach((row) => {
            if (row.trim().length > 0) {
                const cells = row.match(/<td>(.*?)<\/td>/g);
                if (cells && cells.length >= 4) {
                    results.push({
                        x: parseFloat(cells[0].replace(/<\/?td>/g, '')),
                        y: parseFloat(cells[1].replace(/<\/?td>/g, '')),
                        radius: parseInt(cells[2].replace(/<\/?td>/g, '')),
                        hit: cells[3].includes('green'),
                    });
                }
            }
        });

        while (results.length > MAX_POINTS) {
            results.shift();
        }

        results.reverse();
    }

    function drawGraph(radius) {
        context.clearRect(0, 0, canvas.width, canvas.height);

        const scale = scaleConst / radius;

        context.fillStyle = 'rgba(0, 0, 255, 0.5)';

        // 1/4 circle
        context.beginPath();
        context.moveTo(centerX, centerY);
        context.arc(centerX, centerY, radius * scale, Math.PI / 2, 0, true);
        context.closePath();
        context.fill();

        // triangle
        context.beginPath();
        context.moveTo(centerX, centerY);
        context.lineTo(centerX, centerY - radius * scale / 2);
        context.lineTo(centerX - radius * scale, centerY);
        context.closePath();
        context.fill();

        // square
        context.beginPath();
        context.rect(centerX - radius * scale, centerY, radius * scale, radius * scale);
        context.closePath();
        context.fill();

        drawAxes();
        drawAxesSigns(radius);

        console.log(results);
        drawSavedPoints(radius);
    }

    function drawPoint(x, y, radius) {
        const scale = scaleConst / radius;

        const pointX = centerX + x * scale;
        const pointY = centerY - y * scale;

        context.fillStyle = "#FFA500";
        context.beginPath();
        context.arc(pointX, pointY, 4, 0, Math.PI * 2);
        context.fill();
    }

    function drawSavedPoints(radius) {
        results.forEach((result) => {
            drawPoint(result.x, result.y, radius);
        });
    }

    window.drawPoint = function(x, y, radius) {
        results.push({ x, y, radius });

        while (results.length > MAX_POINTS) {
            results.shift();
        }

        drawGraph(radius);
    };

    function drawAxesSigns(radius) {
        let radiusPoints = [
            {value: radius, label: 'R'},
            {value: radius / 2, label: 'R/2'},
            {value: -radius / 2, label: '-R/2'},
            {value: -radius, label: '-R'}
        ];
        let scale = scaleConst / radius;

        // X-axis signs
        for (let i = 0; i < radiusPoints.length; i++) {
            let radiusPoint = radiusPoints[i].value;
            let xPos = radiusPoint * scale + centerX;
            context.beginPath();
            context.moveTo(xPos, centerY - 5);
            context.lineTo(xPos, centerY + 5);
            context.stroke();

            if (radius === 100) context.fillText(radiusPoints[i].label, xPos - 5, centerY + 20);
            else context.fillText(radiusPoint, xPos - 5, centerY + 20);
        }

        // Y-axis signs
        for (let i = 0; i < radiusPoints.length; i++) {
            let radiusPoint = radiusPoints[i].value;
            let yPos = radiusPoint * scale + centerY;
            context.beginPath();
            context.moveTo(centerX - 5, yPos);
            context.lineTo(centerX + 5, yPos);
            context.stroke();

            if (radius === 100) context.fillText(radiusPoints.toReversed()[i].label, centerX + 10, yPos + 5);
            else context.fillText(-radiusPoint, centerX + 10, yPos + 5);
        }

        // 0 sign
        context.fillText('0', centerX + 5, centerY - 5);

        // axis signs
        context.fillText('X', axisLength - 20, centerY - 10);
        context.fillText('Y', centerX + 10, 20);
    }

    function drawAxes() {
        context.strokeStyle = '#000';
        context.lineWidth = 2;

        // X axis
        context.beginPath();
        context.moveTo(0, centerY);
        context.lineTo(axisLength, centerY);
        context.stroke();

        // Y axis
        context.beginPath();
        context.moveTo(centerX, 0);
        context.lineTo(centerX, axisLength);
        context.stroke();

        drawArrows(centerX, 0, 'Y');
        drawArrows(axisLength, centerY, 'X');

        context.font = '14px JetBrains Mono';
        context.fillStyle = '#000';
    }

    function drawArrows(x, y, axis) {
        context.strokeStyle = '#000';
        context.lineWidth = 2;
        context.beginPath();
        if (axis === 'X') {
            context.moveTo(x - 10, y - 5);
            context.lineTo(x, y);
            context.lineTo(x - 10, y + 5);
        } else {
            context.moveTo(x - 5, y + 10);
            context.lineTo(x, y);
            context.lineTo(x + 5, y + 10);
        }
        context.stroke();
    }

    if (selectedRadius) {
        drawGraph(selectedRadius);
    }

    document.querySelectorAll('#r-buttons button').forEach((button) => {
        if (selectedRadius && parseInt(button.value) === selectedRadius) {
            button.setAttribute('selected', 'true');
            button.style.backgroundColor = '#fbfbfb';
        }

        button.addEventListener('click', function () {
            selectedRadius = parseInt(this.value);
            localStorage.setItem('selectedRadius', selectedRadius);

            document.querySelectorAll('#r-buttons button').forEach((btn) => {
                btn.removeAttribute('selected');
                btn.style.backgroundColor = '';
            });

            this.setAttribute('selected', 'true');
            this.style.backgroundColor = '#fbfbfb';

            drawGraph(selectedRadius);
        });
    });
});

document.addEventListener('DOMContentLoaded', function () {

    const table = document.getElementById('result-table');
    const toggleButton = document.getElementById('toggle-table-button');

    toggleButton.addEventListener('click', function () {
        const currentDisplay = getComputedStyle(table).display;
        console.log("currentDisplay: ", currentDisplay)
        if (table.style.display === 'none' || !table.style.display) {
            table.style.display = 'table';
            toggleButton.textContent = 'Скрыть таблицу';
        } else {
            table.style.display = 'none';
            toggleButton.textContent = 'Показать таблицу';
        }
    });
});