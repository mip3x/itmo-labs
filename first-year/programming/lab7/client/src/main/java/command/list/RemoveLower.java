package command.list;

import collection.CollectionService;
import collection.data.StudyGroup;
import command.Command;
import command.InformationStorage;
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
    public String execute(CollectionService collectionService, String username) {
        Long studentsCount = InformationStorage.getReceivedStudyGroup().getStudentsCount();
        List<Integer> elementsToRemove = collectionService.getStudyGroupCollection().stream()
                .filter(studyGroup -> studyGroup.getStudentsCount() < studentsCount)
                .map(StudyGroup::getId)
                .toList();

        if (elementsToRemove.isEmpty()){
            removeLowerCommandLogger.info("No elements smaller than the specified one were found. Nothing has been deleted!");
            return "No elements smaller than the specified one were found. Nothing has been deleted!\"";
        }
        elementsToRemove.forEach(collectionService::removeById);

        removeLowerCommandLogger.error("RemoveLower command executed");
        return "Elements lower than given were successfully deleted!";
    }
}
