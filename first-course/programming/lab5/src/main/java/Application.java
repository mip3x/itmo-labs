import console.ConsoleManager;
import collection.CollectionManager;
import console.InformationStorage;

public class Application {
    public static void main(String[] args) {
        InformationStorage informationStorage = InformationStorage.getInstance();
        CollectionManager collectionManager = CollectionManager.getInstance();
        ConsoleManager consoleManager = ConsoleManager.getInstance(collectionManager, informationStorage);

        consoleManager.init();
    }
}
