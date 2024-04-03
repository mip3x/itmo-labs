package io.console.command.list;

import collection.CollectionManager;

/**
 * Shows all elemets of collection
 */
public class Show extends Command {
    private final CollectionManager collectionManager = CollectionManager.getInstance();

    public Show() {
        super("show", "Вывести все элементы коллекции");
    }

    @Override
    public String execute() {
        return collectionManager.getAllStudyGroupsInfo();
    }
}
