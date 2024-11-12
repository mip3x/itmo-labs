package ru.mip3x.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {
    private final Logger logger = LogManager.getLogger(this.getClass());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var x = request.getParameter("x");
        var y = request.getParameter("y");
        var radius = request.getParameter("radius");

        logger.info("Request data:\nx = {}\ny = {}\nradius = {}", x, y, radius);

        RequestDispatcher requestDispatcher;
        if (x != null && y != null && radius != null) requestDispatcher = request.getRequestDispatcher("/checkData");
        else requestDispatcher = request.getRequestDispatcher("/index.jsp");
        requestDispatcher.forward(request, response);

        logger.info("Forwarding data to {} dispatcher", requestDispatcher.getClass().getName());
    }
}