package console.command;

import console.command.list.*;

import java.util.List;
import java.util.ArrayList;

public class InformationManager {
    private final List<Command> commandsList = new ArrayList<>();
    private final List<Command> history = new ArrayList<>();
    private final int historySize = 14;

    public InformationManager() {
        addCommands();
    }

    public List<Command> getCommandsList() {
        return commandsList;
    }

    public void addToHistory(Command command) {
        history.add(command);
    }

    public List<Command> getHistory() {
        return history;
    }

    public int getHistorySize() {
        return historySize;
    }

    private void addCommands() {
        commandsList.add(new Exit("exit", "Выход из программы", this));
        commandsList.add(new History("history", "История вызовов", this));
        commandsList.add(new Help("help", "Выводит список всех команд", this));
    }
}
