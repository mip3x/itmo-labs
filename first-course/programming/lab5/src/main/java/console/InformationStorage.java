package console;

import collection.CollectionManager;
import collection.data.StudyGroup;
import console.command.list.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

public class InformationStorage {
    private static InformationStorage instance = null;
    private static List<Command> commandsList = new ArrayList<>();
    private static List<Command> commandsRequestingInput = new ArrayList<>();
    private static final int historySize = 14;
    private static final Deque<Command> history = new ArrayDeque<>();
    private static CollectionManager collectionManager;
    private static StudyGroup receivedStudyGroup;

    public static InformationStorage getInstance(CollectionManager collectionManager) {
        if (instance == null) instance = new InformationStorage(collectionManager);
        return instance;
    }

    public InformationStorage(CollectionManager collectionManager) {
        InformationStorage.collectionManager = collectionManager;
        addCommands();
    }

    public static List<Command> getCommandsList() {
        return commandsList;
    }

    public static List<Command> getCommandsRequestingInput() {
        return commandsRequestingInput;
    }

    public void addToHistory(Command command) {
        history.add(command);
    }

    public void setReceivedStudyGroup(StudyGroup studyGroup) {
        receivedStudyGroup = studyGroup;
    }

    public static Deque<Command> getHistory() {
        return history;
    }

    public static int getHistorySize() {
        return historySize;
    }

    public static CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static StudyGroup getReceivedStudyGroup() {
        return receivedStudyGroup;
    }

    private void addCommands() {
        Command addCommand = new Add();

        commandsList = List.of(
                addCommand,
                new Clear(),
                new Exit(),
                new Head(),
                new Help(),
                new History(),
                new Info(),
                new Show()
        );

        commandsRequestingInput = List.of(
               addCommand
        );
    }
}