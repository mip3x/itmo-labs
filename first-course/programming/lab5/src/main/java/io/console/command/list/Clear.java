package io.console.command.list;

import collection.CollectionManager;

public class Clear extends Command {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Clear() {
        super("clear", "Очистить коллекцию");
    }

    @Override
    public String execute() {
        return collectionManager.clearCollection();
    }
}
