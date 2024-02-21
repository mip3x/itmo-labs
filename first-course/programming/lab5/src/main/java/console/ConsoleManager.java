package console;

import console.ConsoleHandler;

public class ConsoleManager {
    private ConsoleHandler consoleHandler;

    public ConsoleManager(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
    }

    public void init() {
        while (true) {
            String line = consoleHandler.receive();
        }
    }
}
