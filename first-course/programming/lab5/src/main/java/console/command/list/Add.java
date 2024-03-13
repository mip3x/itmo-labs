package console.command.list;

import console.InformationStorage;
import collection.CollectionManager;
import collection.data.StudyGroup;

public class Add extends Command {
//    private final CollectionManager collectionManager;

    public Add() {
        super("add", "Добавить новый элемент в коллекцию");
    }

    @Override
    public String execute() {
//        StudyGroup studyGroup = collectionManager.callStudyGroupBuilder();
//        collectionManager.addStudyGroupToCollection(studyGroup);
        return "Add executed";
    } 
}