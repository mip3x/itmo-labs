package io.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ConsoleService implements Runnable {
    private final Logger consoleManagerLogger = LogManager.getLogger();
    private final ConsoleHandler consoleHandler;

    public ConsoleService(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
    }

    @Override
    public void run() {
        while (true) {
            consoleHandler.printPrompt();
            try {
                String inputLine = consoleHandler.receive();

                if (inputLine.equals("save")) {
                    consoleHandler.write(consoleManagerLogger::info, "Deprecated command");
                } else if (inputLine.equals("exit")) {
                    consoleHandler.write(consoleManagerLogger::info, "Server stopped");
                    System.exit(0);
                } else {
                    consoleHandler.write(consoleManagerLogger::debug, inputLine);
                }
            } catch (IOException exception) {
                consoleManagerLogger.error(exception.getMessage());
            }
        }
    }
}
