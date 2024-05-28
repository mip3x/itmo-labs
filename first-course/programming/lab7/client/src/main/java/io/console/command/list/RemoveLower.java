package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Removes elements lower than given
 */
public class RemoveLower extends Command implements RequestingInput {
    private static final Logger removeLowerCommandLogger = LogManager.getLogger();
    public RemoveLower() {
        super("remove_lower {element}", "Delete from collection all elements which are lower than given");
    }

    @Override
    public String execute(CollectionManager collectionManager) {
        Long studentsCount = InformationStorage.getReceivedStudyGroup().getStudentsCount();
        List<Integer> elementsToRemove = collectionManager.getStudyGroupCollection().stream()
                .filter(studyGroup -> studyGroup.getStudentsCount() < studentsCount)
                .map(StudyGroup::getId)
                .toList();

        if (elementsToRemove.isEmpty()){
            removeLowerCommandLogger.info("No elements smaller than the specified one were found. Nothing has been deleted!");
            return "No elements smaller than the specified one were found. Nothing has been deleted!\"";
        }
        elementsToRemove.forEach(collectionManager::removeById);

        removeLowerCommandLogger.error("RemoveLower command executed");
        return "Elements lower than given were successfully deleted!";
    }
}
