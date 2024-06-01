package io.console;

import collection.data.StudyGroup;
import io.console.command.Command;
import io.console.command.list.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Stores information needed for commands
 * Singleton
 */

public class InformationStorage {
    private static InformationStorage instance = null;
    private static List<Command> commandsList = new ArrayList<>();
    private static final int historySize = 14;
    private static final Deque<Command> history = new ArrayDeque<>();
    private static StudyGroup receivedStudyGroup;
    private static List<String> receivedArguments;

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

    public void setReceivedStudyGroup(StudyGroup studyGroup) {
        receivedStudyGroup = studyGroup;
    }

    public void setArguments(List<String> arguments) {
        receivedArguments = arguments;
    }

    public static Deque<Command> getHistory() {
        return history;
    }

    public static int getHistorySize() {
        return historySize;
    }

    public static StudyGroup getReceivedStudyGroup() {
        return receivedStudyGroup;
    }

    public static List<String> getReceivedArguments() {
        return receivedArguments;
    }

    private void addCommands() {
        commandsList = List.of(
                new Add(),
                new Update(),
                new RemoveById(),
                new RemoveAnyByFormOfEducation(),
                new Clear(),
                new Exit(),
                new Head(),
                new Help(),
                new History(),
                new Info(),
                new Show(),
                new GroupCountingBySemesterEnum(),
                new PrintDescending(),
                new RemoveLower(),
                new ExecuteScript(),
                new Test()
        );
    }
}