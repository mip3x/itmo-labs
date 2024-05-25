package io.console.command;

import collection.data.StudyGroup;

import java.io.Serializable;
import java.util.List;

public record CommandDTO(List<String> commandArguments, StudyGroup studyGroup) implements Serializable {
    public CommandDTO(String commandName) {
        this(List.of(commandName));
    }
    public CommandDTO(List<String> commandArguments) {
        this(commandArguments, null);
    }
}
