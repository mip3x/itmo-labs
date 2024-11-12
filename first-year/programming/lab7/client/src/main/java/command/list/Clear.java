package command.list;

import collection.CollectionService;
import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        return collectionService.clearCollection();
    }
}
