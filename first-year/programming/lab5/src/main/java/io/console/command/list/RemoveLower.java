package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Removes elements lower than given
 */
public class RemoveLower extends Command implements RequestingInput {
    private static final Logger removeLowerCommandLogger = LogManager.getLogger();
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

        if (elementsToRemove.isEmpty()){
            removeLowerCommandLogger.info("No elements smaller than the specified one were found. Nothing has been deleted!");
            return "Элементов, меньших заданного, не найдено. Ничего не удалено!";
        }
        elementsToRemove.forEach(collectionManager::removeById);

        removeLowerCommandLogger.error("RemoveLower command executed");
        return "Элементы, меньшие заданного, были успешно удалены!";
    }
}
