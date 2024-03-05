package console.command;

import console.command.list.*;
import console.ConsoleHandler;

import java.util.List;
import java.util.ArrayList;

public class CommandManager {
    private ConsoleHandler consoleHandler;
    private InformationManager informationManager;
    private List<Command> commandsList;

    public CommandManager(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
        informationManager = new InformationManager();
        this.commandsList = informationManager.getCommandsList();
    }

    public void validateCommand(String text) {
        String[] tokens = text.trim().split(" ");

        for (Command command: commandsList) {
            if (command.getName().equals(tokens[0])) {
                String output = command.execute();
                informationManager.addToHistory(command);
                consoleHandler.send(output);
                return;
            }
        }
        consoleHandler.send("Команда не распознана!");
    }
}
