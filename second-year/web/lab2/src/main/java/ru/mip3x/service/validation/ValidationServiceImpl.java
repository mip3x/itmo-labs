package ru.mip3x.service.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mip3x.dto.Request;

public class ValidationServiceImpl implements ValidationService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public Request parseRequestBody(String x, String y, String radius) {
        try {
            int xValidated = Integer.parseInt(x);
            logger.info("x validated: {}", xValidated);
            double yValidated = Double.parseDouble(y);
            logger.info("y validated: {}", yValidated);
            int radiusValidated = Integer.parseInt(radius);
            logger.info("radius validated: {}", radiusValidated);

            return new Request(xValidated, yValidated, radiusValidated);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid request data format");
        }
    }

    public void validateRequestBody(Request requestData) {
        if (requestData.x() < -4 || requestData.x() > 4) throw new IllegalArgumentException("X is out of range");
        if (requestData.y() < -5 || requestData.y() > 5) throw new IllegalArgumentException("Y is out of range");
        if (requestData.radius() < 1 || requestData.radius() > 5) throw new IllegalArgumentException("Radius is out of range");
    }

    public boolean checkHit(Request requestData) {
        logger.info("Validating: \nx: {}\ny: {}\nradius: {}", requestData.x(), requestData.y(), requestData.radius());
        return checkCircle(requestData.x(), requestData.y(), requestData.radius()) || checkRectangle(requestData.x(), requestData.y(), requestData.radius()) || checkTriangle(requestData.x(), requestData.y(), requestData.radius());
    }

    private boolean checkCircle(int x, double y, double radius) {
        return x >= 0 && y <= 0 && (x * x + y * y <= radius * radius);
    }

    private boolean checkRectangle(int x, double y, double radius) {
        return x >= -radius && x <= 0 && y >= -radius && y <= 0;
    }

    private boolean checkTriangle(int x, double y, double radius) {
        return x >= -radius && x <= 0 && y >= 0 && y <= (radius / 2) && y <= 0.5 * (x + radius);
    }
}