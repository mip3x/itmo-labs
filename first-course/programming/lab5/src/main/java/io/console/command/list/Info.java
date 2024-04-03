package io.console.command.list;

import collection.CollectionManager;

/**
 * Returns info about collection
 */
public class Info extends Command {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Info() {
        super("info", "Вывести информацию о коллекции");
    }

    @Override
    public String execute() {
        return collectionManager.getCollectionInfo();
    }
}
