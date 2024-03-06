package console.command;

import console.command.list.Command;
import collection.CollectionManager;

import java.util.List;
import java.util.ArrayList;

public class InformationManager {
    private final List<Command> commandsList;
    private final List<Command> history = new ArrayList<>();
    private final int historySize = 14;
    private static InformationManager instance = null;
    private final CollectionManager collectionManager;

    public static InformationManager getInstance(List<Command> commandsList, CollectionManager collectionManager) {
        if (instance == null) instance = new InformationManager(commandsList, collectionManager);
        return instance;
    }

    public InformationManager(List<Command> commandsList, CollectionManager collectionManager) {
        this.commandsList = commandsList;
        this.collectionManager = collectionManager;
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

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }
}
