package io.console.command.list;

import collection.CollectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Returns the head of collection
 */
public class Head extends Command {
    private final static Logger headCommandLogger = LogManager.getLogger();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Head() {
        super("head", "Вывести первый элемент коллекции");
    }

    @Override
    public String execute() {
        headCommandLogger.trace("Head command executed");
        return collectionManager.getCollectionHead();
    }
}
