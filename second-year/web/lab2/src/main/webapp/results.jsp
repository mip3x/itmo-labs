<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Результаты</title>
    <link rel="stylesheet" href="static/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>Результаты</h1>
        <a href="index.jsp">Назад к графику</a>
    </header>
    <main>
        <table>
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
                    if (results != null && !results.isEmpty()) {
                        for (String row : results) {
                            out.print(row);
                        }
                    } else {
                %>
                <tr>
                    <td colspan="4">Нет данных для отображения</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </main>
</div>
</body>
</html>
