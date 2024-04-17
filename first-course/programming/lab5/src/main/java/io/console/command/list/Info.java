package io.console.command.list;

import collection.CollectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Returns info about collection
 */
public class Info extends Command {
    private static final Logger infoCommandLogger = LogManager.getLogger();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Info() {
        super("info", "Вывести информацию о коллекции");
    }

    @Override
    public String execute() {
        infoCommandLogger.trace("Info command executed");
        return collectionManager.getCollectionInfo();
    }
}
