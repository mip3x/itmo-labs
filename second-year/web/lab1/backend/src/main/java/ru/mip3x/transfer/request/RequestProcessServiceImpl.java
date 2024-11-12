package ru.mip3x.transfer.request;

import com.fastcgi.FCGIInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mip3x.dto.RequestData;
import ru.mip3x.dto.Response;
import ru.mip3x.validation.ValidationService;
import ru.mip3x.validation.ValidationServiceImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RequestProcessServiceImpl implements RequestProcessService {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final ValidationService validationService;

    public RequestProcessServiceImpl() {
        validationService = new ValidationServiceImpl();
    }

    public Response processRequest() throws IOException {
        String requestMethod = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        logger.info("Request method: {}", requestMethod);

        return switch (requestMethod) {
            case "POST" -> {
                String requestBody = getRequestBody();
                logger.info("Request body:{}", requestBody);

                try {
                    RequestData requestData = validationService.parseRequestBody(requestBody);
                    validationService.validateRequestBody(requestData);

                    boolean checkHitStatus = validationService.checkHit(requestData);
                    logger.info("Result: {}", checkHitStatus);

                    yield new Response(200, "OK", requestData, checkHitStatus);
                }
                catch (IllegalArgumentException exception) {
                    logger.error("Data validating failed", exception);
                    yield new Response(400, "Bad Request");
                }
            }
            default -> new Response(400, "Bad Request");
        };
    }

    private String getRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();
        var contentLength = FCGIInterface.request.inStream.available();
        var buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);

        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();

        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }
}
