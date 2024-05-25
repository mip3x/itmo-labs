package io.console.command;

import collection.data.StudyGroup;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record CommandDTO(List<String> commandArguments, StudyGroup studyGroup) implements Serializable {
    public CommandDTO(List<String> commandArguments) {
        this(commandArguments, null);
    }
}
