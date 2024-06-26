package io.console.command.list;

import collection.CollectionManager;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Returns the head of collection
 */
public class Head extends Command {
    private final static Logger headCommandLogger = LogManager.getLogger();
    public Head() {
        super("head", "Print out first element of collection");
    }

    @Override
    public String execute(CollectionManager collectionManager) {
        headCommandLogger.trace("Head command executed");
        return collectionManager.getCollectionHead();
    }
}
