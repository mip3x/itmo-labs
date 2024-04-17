package io.console.command.list;

import collection.CollectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shows all elemets of collection
 */
public class Show extends Command {
    private static final Logger showCommandLogger = LogManager.getLogger();
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    public Show() {
        super("show", "Вывести все элементы коллекции");
    }

    @Override
    public String execute() {
        showCommandLogger.trace("Show command executed");
        return collectionManager.getAllStudyGroupsInfo();
    }
}
