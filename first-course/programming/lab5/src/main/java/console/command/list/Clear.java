package console.command.list;

import console.command.InformationManager;
import collection.CollectionManager;

import java.util.List;

public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(String name, String description, InformationManager informationManager) {
        super(name, description, informationManager);
        this.collectionManager = informationManager.getCollectionManager();
    }

    @Override
    public String execute() {
        return collectionManager.clearCollection();
    } 
}
