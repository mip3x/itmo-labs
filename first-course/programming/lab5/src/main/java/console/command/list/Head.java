package console.command.list;

import console.InformationStorage;
import collection.CollectionManager;

public class Head extends Command {
    public Head() {
        super("head", "Вывести первый элемент коллекции");
    }

    @Override
    public String execute() {
//        return collectionManager.getCollectionHead();
        return "Head executed";
    }
}
