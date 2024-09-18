package ru.mip3x.validation;

import ru.mip3x.dto.ValidatedData;
import ru.mip3x.server.Server;

import java.util.logging.Logger;

public class ValidationServiceImpl implements ValidationService {
    private final Logger logger;

    public ValidationServiceImpl() {
        logger = Server.getLogger();
    }

    public ValidatedData validateRequestBody(String request) {
        try {
            int x = Integer.parseInt(request.split("&")[0].split("=")[1]);
            if (x < -5 || x > 3) return null;

            double y = Double.parseDouble(request.split("&")[1].split("=")[1]);
            if (y < -3 || y > 5) return null;

            int radius = Integer.parseInt(request.split("&")[2].split("=")[1]);
            if (radius < 1 || radius > 5) return null;

            return new ValidatedData(x, y, radius);

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public boolean checkHit(ValidatedData request) {
        int x = request.x();
        double y = request.y();
        double radius = request.radius();

        logger.info("VALIDATING: \n" + "x: " + x + "\ny: " + y + "\nradius: " + radius);
        logger.info("RESULT IS: " + (checkCircle(x, y, radius) || checkRectangle(x, y, radius) || checkTriangle(x, y, radius)));

        return checkCircle(x, y, radius) || checkRectangle(x, y, radius) || checkTriangle(x, y, radius);
    }

    private boolean checkCircle(int x, double y, double radius) {
        return x >= 0 && y >= 0 && (x * x + y * y <= radius * radius);
    }

    private boolean checkRectangle(int x, double y, double radius) {
        return x >= -radius && x <= 0 && y >= -radius && y <= 0;
    }

    private boolean checkTriangle(int x, double y, double radius) {
        return x >= -radius && x <= 0 && y >= 0 && y <= radius && y <= x + radius;
    }
}
