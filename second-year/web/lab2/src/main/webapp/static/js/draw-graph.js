document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('graph');
    const context = canvas.getContext('2d');
    let selectedRadius = 100;

    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;

    const axisLength = canvas.width;
    const scaleConst = 130;

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
    }

    function drawPoint(x, y, radius) {
        const scale = scaleConst / radius;
        const pointX = centerX + x * scale;
        const pointY = centerY - y * scale;

        context.fillStyle = '#FFA500';
        context.beginPath();
        context.arc(pointX, pointY, 4, 0, Math.PI * 2);
        context.fill();
    }
    window.drawPoint = drawPoint;

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

    document.querySelectorAll('#r-buttons button').forEach(button => {
        button.addEventListener('click', function () {
            selectedRadius = parseInt(this.value);
            // drawAxesSigns(selectedRadius)
            drawGraph(selectedRadius);
        });
    });

    drawGraph(selectedRadius);
});

document.addEventListener('DOMContentLoaded', function () {

    const table = document.getElementById('result-table');
    const toggleButton = document.getElementById('toggle-table-button');

    toggleButton.addEventListener('click', function () {
        if (table.style.display === 'none' || !table.style.display) {
            table.style.display = 'table';
            toggleButton.textContent = 'Скрыть таблицу';
        } else {
            table.style.display = 'none';
            toggleButton.textContent = 'Показать таблицу';
        }
    });
});
