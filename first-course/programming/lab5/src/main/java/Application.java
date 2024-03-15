import console.ConsoleManager;
import collection.CollectionManager;
import console.InformationStorage;

public class Application {
    public static void main(String[] args) {
        CollectionManager collectionManager = CollectionManager.getInstance();
        InformationStorage informationStorage = InformationStorage.getInstance(collectionManager);
        ConsoleManager consoleManager = ConsoleManager.getInstance(informationStorage);

        consoleManager.init();
    }
}
