package console;

import console.ConsoleHandler;
import console.command.CommandManager;
import collection.CollectionManager;
import exception.InvalidInputException;

public class ConsoleManager {
    private final ConsoleHandler consoleHandler;
    private final CommandManager commandManager;

    public ConsoleManager(CollectionManager collectionManager) {
        this.consoleHandler = new ConsoleHandler();
        this.commandManager = new CommandManager(consoleHandler, collectionManager);
    }

    public void init() {
        while (true) {
            try {
                consoleHandler.printPrompt();
                String line = consoleHandler.receive();
                commandManager.executeCommand(line);
            }
            catch (InvalidInputException exception) {
                consoleHandler.sendWithNewLine(exception.getMessage());
            }
        }
    }
}
