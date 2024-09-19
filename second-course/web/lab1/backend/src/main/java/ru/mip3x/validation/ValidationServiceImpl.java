package ru.mip3x.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mip3x.dto.RequestData;

public class ValidationServiceImpl implements ValidationService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public RequestData parseRequestBody(String requestBody) {
        try {
            int x = Integer.parseInt(requestBody.split("&")[0].split("=")[1]);
            double y = Double.parseDouble(requestBody.split("&")[1].split("=")[1]);
            int radius = Integer.parseInt(requestBody.split("&")[2].split("=")[1]);

            return new RequestData(x, y, radius);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("Invalid request format", exception);
        }
    }

    public void validateRequestBody(RequestData requestData) {
        if (requestData.x() < -5 || requestData.x() > 3) throw new IllegalArgumentException("X is out of range");
        if (requestData.y() < -3 || requestData.y() > 5) throw new IllegalArgumentException("Y is out of range");
        if (requestData.radius() < 1 || requestData.radius() > 5) throw new IllegalArgumentException("Radius is out of range");
    }

    public boolean checkHit(RequestData requestData) {
        logger.info("Validating: \nx: {}\ny: {}\nradius: {}", requestData.x(), requestData.y(), requestData.radius());
        return checkCircle(requestData.x(), requestData.y(), requestData.radius()) || checkRectangle(requestData.x(), requestData.y(), requestData.radius()) || checkTriangle(requestData.x(), requestData.y(), requestData.radius());
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
