package console;

import collection.CollectionManager;
import console.command.list.Command;
import exception.InvalidInputException;

public class ConsoleManager {
    private static ConsoleManager instance = null;
    private final ConsoleHandler consoleHandler;
    private final InformationStorage informationStorage;

    public static ConsoleManager getInstance(InformationStorage informationStorage) {
        if (instance == null) instance = new ConsoleManager(informationStorage);
        return instance;
    }

    public ConsoleManager(InformationStorage informationStorage) {
        this.informationStorage = informationStorage;
        this.consoleHandler = new ConsoleHandler();
    }

    public void init() {
        String inputLine;

        while ((inputLine = consoleHandler.receive(true)) != null) {
            process(inputLine);
        }
    }

    private void process(String inputLine) {
        try {
            String[] tokens = inputLine.trim().split(" ");

            for (Command command: InformationStorage.getCommandsList()) {
                if (command.getName().equals(tokens[0])) {
                    String output = command.execute();
                    informationStorage.addToHistory(command);
                    consoleHandler.sendWithNewLine(output);
                    return;
                }
            }
            throw new InvalidInputException("Команда не распознана!");
        } catch (Exception exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
        }
    }
}