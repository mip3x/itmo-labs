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
        super("remove_lower {number}", "Delete from collection all elements which are lower than given");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        Long studentsCount = Long.valueOf(InformationStorage.getReceivedArguments().get(0));
        List<Integer> elementsToRemove = collectionService.getCollection().stream()
                .filter(studyGroup -> studyGroup.getStudentsCount() < studentsCount)
                .map(StudyGroup::getId)
                .toList();

        if (elementsToRemove.isEmpty()) {
            removeLowerCommandLogger.info("No elements smaller than the specified one were found. Nothing has been deleted");
            return "No elements smaller than the specified one were found. Nothing has been deleted\"";
        }

        StringBuilder response = new StringBuilder();
        for (int id : elementsToRemove)
            response.append(collectionService.removeById(id, username)).append("\n");

        if (!response.isEmpty()) response.deleteCharAt(response.length() - 1);

        removeLowerCommandLogger.info("RemoveLower command executed");
        return response.toString();
    }
}
