package io.console.command.list;

import io.console.InformationStorage;
import collection.CollectionManager;

public class Head extends Command {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Head() {
        super("head", "Вывести первый элемент коллекции");
    }

    @Override
    public String execute() {
        return collectionManager.getCollectionHead();
    }
}
