package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Adds element to collection
 */
public class Add extends Command implements RequestingInput {
    private static final Logger addCommandLogger = LogManager.getLogger();
    public Add() {
        super("add {element}", "Add new element to collection");
    }

    @Override
    public String execute(CollectionManager collectionManager) {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        if (studyGroup != null) return collectionManager.addStudyGroupToCollection(studyGroup);
        throw new NullPointerException("studyGroup is null!");
    }
}