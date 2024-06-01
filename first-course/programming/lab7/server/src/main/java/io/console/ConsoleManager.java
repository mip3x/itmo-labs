package io.console;

import collection.CollectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ConsoleManager implements Runnable {
    private final Logger consoleManagerLogger = LogManager.getLogger();
    private final ConsoleHandler consoleHandler;

    public ConsoleManager(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
    }

    @Override
    public void run() {
        while (true) {
            consoleHandler.printPrompt();
            try {
                String inputLine = consoleHandler.receive();

                if (inputLine.equals("save")) {
                    String message = CollectionManager.getInstance().saveCollection();
                    consoleHandler.write(consoleManagerLogger::info, message);
                } else if (inputLine.equals("exit")) {
                    String message = CollectionManager.getInstance().saveCollection();
                    consoleHandler.write(consoleManagerLogger::info, message);
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
