package io.console.command.list;

import collection.CollectionManager;
import io.console.InformationStorage;
import collection.data.StudyGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Adds element to collection
 */
public class Add extends Command implements RequestingInput {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    private static final Logger addCommandLogger = LogManager.getLogger();
    public Add() {
        super("add {element}", "Добавить новый элемент в коллекцию");
    }

    @Override
    public String execute() {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        addCommandLogger.trace("Add command executed");
        return collectionManager.addStudyGroupToCollection(studyGroup);
    }
}