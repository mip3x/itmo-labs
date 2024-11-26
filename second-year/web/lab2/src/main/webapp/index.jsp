<%@ page import="java.util.List" %>
<%@ page import="ru.mip3x.dto.Response" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лабораторная работа - Проверка попадания точки</title>
    <link rel="stylesheet" href="static/css/style.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400&display=swap">
</head>
<body>
<div class="container">
    <header>
        <div class="credentials">
            ФИО: Иванов Иван Иванович<br>
            Группа: P3211<br>
            Вариант: 21108
        </div>
    </header>

    <main>
        <div class="graph-data">
            <div class="canvas-container">
                <canvas id="graph" width="400" height="400"></canvas>
            </div>

            <form id="point-form" action="checkData" method="get">
                <h4>X</h4>
                <div class="checkbox-group" id="x-checkboxes" style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px;">
                    <label><input type="checkbox" name="x" value="-4">-4</label>
                    <label><input type="checkbox" name="x" value="-3">-3</label>
                    <label><input type="checkbox" name="x" value="-2">-2</label>
                    <label><input type="checkbox" name="x" value="-1">-1</label>
                    <label><input type="checkbox" name="x" value="0">0</label>
                    <label><input type="checkbox" name="x" value="1">1</label>
                    <label><input type="checkbox" name="x" value="2">2</label>
                    <label><input type="checkbox" name="x" value="3">3</label>
                    <label><input type="checkbox" name="x" value="4">4</label>
                </div>

                <label for="y"></label>
                <input type="number" id="y" name="y" step="0.1" min="-5" max="5" placeholder="Введите Y [-5; 5]" required>

                <h4>R</h4>
                <div class="button-group" id="r-buttons">
                    <button type="button" value="1">1</button>
                    <button type="button" value="2">2</button>
                    <button type="button" value="3">3</button>
                    <button type="button" value="4">4</button>
                    <button type="button" value="5">5</button>
                </div>

                <input type="submit" value="Проверить точку">
                <p class="error" id="error-msg"></p>
            </form>
    </div>

    <script>
        const savedResults = `<%= session.getAttribute("results") != null
            ? String.join("", (List<String>) session.getAttribute("results"))
            : "" %>`;
    </script>

    <script src="static/js/draw-graph.js"></script>
    <script src="static/js/script.js"></script>

    <button id="toggle-table-button" class="toggle-btn">Показать таблицу</button>
    <table id="result-table" style="display: none;">
        <thead>
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>Результат</th>
                <th>Время отправки</th>
                <th>Время выполнения</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<String> results = (List<String>) session.getAttribute("results");
                if (results != null) {
                    for (String row : results) {
                        out.print(row);
                    }
                }
            %>
        </tbody>
    </table>
  </main>
</div>
</body>
</html>
