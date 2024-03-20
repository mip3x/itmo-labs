package io.console.command.list;

import io.console.InformationStorage;
import collection.CollectionManager;

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
