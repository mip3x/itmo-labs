package ru.mip3x;

import java.io.*;
import java.util.Enumeration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/ifmo-test")
public class IfmoServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "ifmo-test welcome message";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            System.out.println(attributeName + " = " + session.getAttribute(attributeName));
        }
        session.setAttribute("one", "two");
        System.out.println(session.getMaxInactiveInterval());
//        Cookie cookieTest = new Cookie("message", message);
//        response.addCookie(cookieTest);
//
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                System.out.println(cookie.getName() + ": " + cookie.getValue());
//            }
//        } else {
//            System.out.println("No available cookies.");
//        }
//
//        response.setContentType("text/plain");
//        PrintWriter out = response.getWriter();
//        out.println("Cookies processed successfully.");

//        Enumeration<String> headers = request.getHeaderNames();
//        while (headers.hasMoreElements()) {
//            String header = headers.nextElement();
//            System.out.println(header + " : " + request.getHeader(header));
//        }
//        System.out.println();
//        response.setContentType("text/html");
//
//        PrintWriter out = response.getWriter();
//        out.println("<html><body>");
//        out.println("<h1>" + message + "</h1>");
//        out.println("</body></html>");
    }
}