package io.console.command.list;

import collection.CollectionManager;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Saves collection to file
 */
public class Save extends Command {
    private static final Logger saveCommandLogger = LogManager.getLogger();
    public Save() {
        super("save", "Save collection to file");
    }

    @Override
    public String execute(CollectionManager collectionManager) {
        if (collectionManager.getStudyGroupCollection() == null)
        {
            saveCommandLogger.info("Collection is empty. Nothing to save");
            saveCommandLogger.trace("Save command executed");
            return "Collection is empty! Nothing to save!";
        }
        try {
//            FileManager.saveCollection();
            saveCommandLogger.info("Collection has been saved");
            saveCommandLogger.trace("Save command executed");
            return "Collection has been successfully saved!";
        }
        catch (Exception exception) {
            saveCommandLogger.error("Saving error");
            return exception.getMessage();
        }
    }
}
