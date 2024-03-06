package console.command;

import console.command.list.*;
import console.ConsoleHandler;
import collection.CollectionManager;

import java.util.List;
import java.util.ArrayList;

public class CommandManager {
    private final ConsoleHandler consoleHandler;
    private final InformationManager informationManager;
    private List<Command> commandsList = new ArrayList<>();

    public CommandManager(ConsoleHandler consoleHandler, CollectionManager collectionManager) {
        this.consoleHandler = consoleHandler;
        informationManager = InformationManager.getInstance(commandsList, collectionManager);
        addCommands();
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
        commandsList.add(new Exit("exit", "Выйти из программы", informationManager));
        commandsList.add(new History("history", "Вывести историю вызовов", informationManager));
        commandsList.add(new Help("help", "Вывести список всех команд", informationManager));
        commandsList.add(new Add("add", "Добавить новый элемент в коллекцию", informationManager));
        commandsList.add(new Info("info", "Вывести информацию о коллекции", informationManager));
        commandsList.add(new Head("head", "Вывести первый элемент коллекции", informationManager));
        commandsList.add(new Clear("clear", "Очистить коллекцию", informationManager));
    }
}
