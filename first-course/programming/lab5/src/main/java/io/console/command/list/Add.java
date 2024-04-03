package io.console.command.list;

import collection.CollectionManager;
import io.console.InformationStorage;
import collection.data.StudyGroup;

/**
 * Adds element to collection
 */
public class Add extends Command implements RequestingInput {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Add() {
        super("add {element}", "Добавить новый элемент в коллекцию");
    }

    @Override
    public String execute() {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        return collectionManager.addStudyGroupToCollection(studyGroup);
    }
}