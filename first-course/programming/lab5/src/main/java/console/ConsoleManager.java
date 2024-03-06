package console;

import console.ConsoleHandler;
import console.command.CommandManager;
import collection.CollectionManager;

public class ConsoleManager {
    private final ConsoleHandler consoleHandler;
    private final CommandManager commandManager;

    public ConsoleManager(CollectionManager collectionManager) {
        this.consoleHandler = new ConsoleHandler();
        this.commandManager = new CommandManager(consoleHandler, collectionManager);
    }

    public void init() {
        while (true) {
            String line = consoleHandler.receive();
            commandManager.executeCommand(line);
        }
    }
}
