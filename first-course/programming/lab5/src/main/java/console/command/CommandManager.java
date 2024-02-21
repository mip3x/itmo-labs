package console.command;

import console.command.list.*;
import console.ConsoleHandler;

import java.util.List;
import java.util.ArrayList;

public class CommandManager {
    private ConsoleHandler consoleHandler;
    private final List<Command> commandsList = new ArrayList<>();

    public CommandManager(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
        addCommands();
    }

    public void validateCommand(String text) {
        String[] tokens = text.trim().split(" ");

        for (Command command: commandsList) {
            if (command.getName().equals(tokens[0])) {
                String output = command.execute();
                consoleHandler.send(output);
                return;
            }
        }
        consoleHandler.send("Команда не распознана!");
    }

    public List<Command> getCommandsList() {
        return commandsList;
    }

    private void addCommands() {
        commandsList.add(new Exit("exit", "Выход из программы"));
        commandsList.add(new Help("help", "Выводит список всех команд", commandsList));
    }
}
