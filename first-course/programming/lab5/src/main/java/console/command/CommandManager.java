package console.command;

import console.command.list.*;
import console.ConsoleHandler;

import java.util.List;
import java.util.ArrayList;

public class CommandManager {
    private ConsoleHandler consoleHandler;
    private final List<Command> commandsList = new ArrayList<>();
    private final List<Command> history = new ArrayList<>();
    private final int historySize = 14;

    public CommandManager(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
        addCommands();
    }

    public void validateCommand(String text) {
        String[] tokens = text.trim().split(" ");

        for (Command command: commandsList) {
            if (command.getName().equals(tokens[0])) {
                String output = command.execute();
                history.add(command);
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
        commandsList.add(new History("history", "История вызовов", history, historySize));
        commandsList.add(new Help("help", "Выводит список всех команд", commandsList));
        commandsList.add(new Help("lol", "Выводит список всех команд", commandsList));
        commandsList.add(new Help("lol1", "Выводит список всех команд", commandsList));
    }
}
