package command.list;

import collection.CollectionService;
import collection.data.StudyGroup;
import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Clears collection
 */
public class Clear extends Command {
    private static final Logger clearCommandLogger = LogManager.getLogger();
    public Clear() {
        super("clear", "Clear collection");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        clearCommandLogger.trace("Clear command executed");
        List<Integer> elementsToRemove = collectionService.getCollection().stream()
                .filter(studyGroup -> studyGroup.getCreator().equals(username))
                .map(StudyGroup::getId)
                .toList();

        if (elementsToRemove.isEmpty()) {
            clearCommandLogger.info("No elements with such creator were found. Nothing has been deleted!");
            return "No elements with such creator were found. Nothing has been deleted!";
        }

        StringBuilder response = new StringBuilder();
        for (int id : elementsToRemove)
            response.append(collectionService.removeById(id, username)).append("\n");

        if (!response.isEmpty()) response.deleteCharAt(response.length() - 1);

        clearCommandLogger.info("Clear command executed");
        return response.toString();
    }
}
