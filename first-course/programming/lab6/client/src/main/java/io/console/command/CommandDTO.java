package io.console.command;

import collection.data.StudyGroup;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record CommandDTO(String commandName, List<String> commandArguments, StudyGroup studyGroup) implements Serializable {
    public CommandDTO(String commandName) {
        this(commandName, Collections.emptyList(), null);
    }
    public CommandDTO(String commandName, List<String> commandArguments) {
        this(commandName, commandArguments, null);
    }
}
