package console.command.list;

import collection.CollectionManager;
import console.InformationStorage;
import collection.data.StudyGroup;

public class Add extends Command {
    private final CollectionManager collectionManager = InformationStorage.getCollectionManager();
    public Add() {
        super("add", "Добавить новый элемент в коллекцию");
    }

    @Override
    public String execute() {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        return collectionManager.addStudyGroupToCollection(studyGroup);
    }
}