package console.command.list;

import console.InformationStorage;
import collection.CollectionManager;

public class Clear extends Command {
    private final CollectionManager collectionManager = InformationStorage.getCollectionManager();
    public Clear() {
        super("clear", "Очистить коллекцию");
    }

    @Override
    public String execute() {
        return collectionManager.clearCollection();
    }
}
