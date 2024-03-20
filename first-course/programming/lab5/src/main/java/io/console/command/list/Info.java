package io.console.command.list;

import io.console.InformationStorage;
import collection.CollectionManager;

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
