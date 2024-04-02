package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;

import java.util.List;

public class RemoveLower extends Command implements RequestingInput {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public RemoveLower() {
        super("remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный");
    }

    @Override
    public String execute() {
        Long studentsCount = InformationStorage.getReceivedStudyGroup().getStudentsCount();
        List<Integer> elementsToRemove = collectionManager.getStudyGroupCollection().stream()
                .filter(studyGroup -> studyGroup.getStudentsCount() < studentsCount)
                .map(StudyGroup::getId)
                .toList();

        if (elementsToRemove.isEmpty()) return "Элементов, меньших заданного, не найдено. Ничего не удалено!";
        elementsToRemove.forEach(collectionManager::removeById);
        return "Элементы, меньшие заданного, были успешно удалены!";
    }
}
