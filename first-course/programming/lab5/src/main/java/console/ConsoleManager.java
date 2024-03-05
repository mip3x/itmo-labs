package console;

import console.ConsoleHandler;
import console.command.CommandManager;

public class ConsoleManager {
    private ConsoleHandler consoleHandler;
    private CommandManager commandManager;

    public ConsoleManager(ConsoleHandler consoleHandler, CommandManager commandManager) {
        this.consoleHandler = consoleHandler;
        this.commandManager = commandManager;
    }

    public void init() {
        while (true) {
            String line = consoleHandler.receive();
            commandManager.executeCommand(line);
        }
    }
}
