package console.command;

import console.command.list.*;
import console.ConsoleHandler;

import java.util.List;
import java.util.ArrayList;

public class CommandManager {
    private ConsoleHandler consoleHandler;
    private InformationManager informationManager;
    private List<Command> commandsList = new ArrayList<>();

    public CommandManager(ConsoleHandler consoleHandler) {
        this.consoleHandler = consoleHandler;
        informationManager = InformationManager.getInstance(commandsList);
        addCommands();
        this.commandsList = informationManager.getCommandsList();
    }

    public void executeCommand(String text) {
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

    private void addCommands() {
        commandsList.add(new Exit("exit", "Выход из программы", informationManager));
        commandsList.add(new History("history", "История вызовов", informationManager));
        commandsList.add(new Help("help", "Выводит список всех команд", informationManager));
    }
}
