package ru.mip3x.transfer.request;

import com.fastcgi.FCGIInterface;
import ru.mip3x.dto.Response;
import ru.mip3x.dto.ValidatedData;
import ru.mip3x.server.Server;
import ru.mip3x.transfer.response.SendResponseService;
import ru.mip3x.transfer.response.SendResponseServiceImpl;
import ru.mip3x.validation.ValidationService;
import ru.mip3x.validation.ValidationServiceImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class RequestProcessServiceImpl implements RequestProcessService {
    private final Logger logger;
    private final ValidationService validationService;
    private final SendResponseService sendResponseService;

    public RequestProcessServiceImpl() {
        logger = Server.getLogger();
        validationService = new ValidationServiceImpl();
        sendResponseService = new SendResponseServiceImpl();
    }

    public void processRequest(long executionStart) throws IOException {
        String requestMethod = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        logger.info("REQUEST_METHOD: \n" + requestMethod);

        Response response;
        if (requestMethod.equals("POST")) {
            String requestBody = getRequestBody();
            logger.info("REQUEST BODY:" + requestBody);

            ValidatedData validatedData = validationService.validateRequestBody(requestBody);

            if (validatedData == null) response = new Response(400, "Bad Request");
            else response = new Response(200, "OK", validatedData, validationService.checkHit(validatedData), executionStart);
        }
        else response = new Response(400, "Bad Request");

        logger.info("GOING TO SEND RESPONSE...");
        sendResponseService.sendResponse(response);
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
