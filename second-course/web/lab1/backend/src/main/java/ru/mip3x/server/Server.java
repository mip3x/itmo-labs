package ru.mip3x.server;

import com.fastcgi.FCGIInterface;
import ru.mip3x.transfer.request.RequestProcessService;
import ru.mip3x.transfer.request.RequestProcessServiceImpl;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private static Logger logger;
    private final FCGIInterface fcgiInterface;
    private final RequestProcessService requestProcessService;

    public Server() {
        logger = Logger.getLogger(Server.class.getName());

        requestProcessService = new RequestProcessServiceImpl();
        fcgiInterface = new FCGIInterface();
    }

    public static Logger getLogger() {
        return logger;
    }

    public void run() {
        logger.info("FCGI SERVER STARTED");
        logger.info("WAITING FOR REQUEST...");

        while (fcgiInterface.FCGIaccept() >= 0) {
            long executionStart = System.nanoTime();

            try {
                requestProcessService.processRequest(executionStart);
            } catch (IOException e) {
                logger.warning("REQUEST PROCESS FAILED");
            }
        }
    }
}