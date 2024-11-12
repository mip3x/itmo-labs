package io.console.command.list;

import collection.CollectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clears collection
 */
public class Clear extends Command {
    private static final Logger clearCommandLogger = LogManager.getLogger();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Clear() {
        super("clear", "Очистить коллекцию");
    }

    @Override
    public String execute() {
        clearCommandLogger.trace("Clear command executed");
        return collectionManager.clearCollection();
    }
}
