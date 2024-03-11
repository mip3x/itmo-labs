package console;

import console.ConsoleHandler;
import console.command.CommandManager;
import collection.CollectionManager;
import exception.InvalidInputException;

public class ConsoleManager {
    private static ConsoleManager instance = null;
    private final ConsoleHandler consoleHandler;
    private final CommandManager commandManager;

    public static ConsoleManager getInstance(CollectionManager collectionManager) {
        if (instance == null) instance = new ConsoleManager(collectionManager);
        return instance;
    }

    public ConsoleManager(CollectionManager collectionManager) {
        this.consoleHandler = new ConsoleHandler();
        this.commandManager = CommandManager.getInstance(consoleHandler, collectionManager);
    }

    public void init() {
        String inputLine;

        while ((inputLine = consoleHandler.receive(true)) != null) {
            process(inputLine);
        }
    }

    private void process(String inputLine) {
        try {
            commandManager.executeCommand(inputLine);
        } catch (Exception exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
        }
    }
}
