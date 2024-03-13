package console;

import console.command.list.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

public class InformationStorage {
    private static List<Command> commandsList = new ArrayList<>();
    private static final int historySize = 14;
    private static InformationStorage instance = null;
    private static final Deque<Command> history = new ArrayDeque<>();

    public static InformationStorage getInstance() {
        if (instance == null) instance = new InformationStorage();
        return instance;
    }

    public InformationStorage() {
        addCommands();
    }

    public static List<Command> getCommandsList() {
        return commandsList;
    }

    public void addToHistory(Command command) {
        history.add(command);
    }

    public static Deque<Command> getHistory() {
        return history;
    }

    public static int getHistorySize() {
        return historySize;
    }

    private void addCommands() {
        commandsList = List.of(
                new Add(),
                new Clear(),
                new Exit(),
                new Head(),
                new Help(),
                new History(),
                new Info(),
                new Show()
        );
    }
}
