package console.command.list;

import console.InformationStorage;
import collection.CollectionManager;

public class Info extends Command {
    private final CollectionManager collectionManager = InformationStorage.getCollectionManager();
    public Info() {
        super("info", "Вывести информацию о коллекции");
    }

    @Override
    public String execute() {
        return collectionManager.getCollectionInfo();
    }
}
