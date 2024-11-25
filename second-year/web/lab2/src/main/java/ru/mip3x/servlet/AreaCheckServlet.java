package ru.mip3x.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mip3x.dto.Request;
import ru.mip3x.service.validation.ValidationService;
import ru.mip3x.service.validation.ValidationServiceImpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/checkData")
public class AreaCheckServlet extends HttpServlet {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        ValidationService validationService = new ValidationServiceImpl();
        long executionStart = System.nanoTime();
        final String time = DATE_FORMAT.format(new Date());
        boolean checkHitStatus;
        Request requestData;
        List<String> results = (List<String>) session.getAttribute("results");

        if (results == null) {
            results = new ArrayList<>();
            session.setAttribute("results", results);
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        try {
            requestData = validationService.parseRequestBody(
                    request.getParameter("x"),
                    request.getParameter("y"),
                    request.getParameter("radius"));
            validationService.validateRequestBody(requestData);
            checkHitStatus = validationService.checkHit(requestData);
            logger.info("Check hit status: {}", checkHitStatus);

            String result =
                    """
                    <tr>
                    <td>%d</td>
                    <td>%.1f</td>
                    <td>%d</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%tS ms</td>
                    </tr>
                    """.formatted(
                            requestData.x(), requestData.y(), requestData.radius(),
                            checkHitStatus ? "<span style=\"color: green;\">&#10004;</span>" : "<span style=\"color: red;\">&#10008;</span>"
                            , time, System.nanoTime() - executionStart);

            results.addFirst(result);
            if (results.size() > 10) results.removeLast();
            response.setContentType("text/html");

            request.setAttribute("results", results);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (IllegalArgumentException exception) {
            logger.info("Error occurred while validating: {}", exception.getMessage());
            response.getWriter().println(exception.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            requestDispatcher.forward(request, response);
        }
    }
}