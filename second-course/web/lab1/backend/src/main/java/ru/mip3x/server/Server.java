package ru.mip3x.server;

import com.fastcgi.FCGIInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mip3x.dto.Response;
import ru.mip3x.transfer.request.RequestProcessService;
import ru.mip3x.transfer.request.RequestProcessServiceImpl;
import ru.mip3x.transfer.response.SendResponseService;
import ru.mip3x.transfer.response.SendResponseServiceImpl;

import java.io.IOException;

public class Server {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final FCGIInterface fcgiInterface;
    private final RequestProcessService requestProcessService;
    private final SendResponseService sendResponseService;

    public Server() {
        requestProcessService = new RequestProcessServiceImpl();
        sendResponseService = new SendResponseServiceImpl();
        fcgiInterface = new FCGIInterface();
    }

    public void run() {
        logger.info("FastCGI server started");
        logger.info("Waiting for request...");

        while (fcgiInterface.FCGIaccept() >= 0) {
            long executionStart = System.nanoTime();

            try {
                Response response = requestProcessService.processRequest();
                logger.info("Going to send response...");
                sendResponseService.sendResponse(response, executionStart);
            } catch (IOException exception) {
                logger.error("Request process failed", exception);
            }
        }
    }
}