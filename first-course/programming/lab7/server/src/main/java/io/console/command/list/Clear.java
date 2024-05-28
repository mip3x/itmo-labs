package io.console.command.list;

import collection.CollectionManager;
import io.console.command.Command;
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
    public String execute(CollectionManager collectionManager) {
        clearCommandLogger.trace("Clear command executed");
        return collectionManager.clearCollection();
    }
}
