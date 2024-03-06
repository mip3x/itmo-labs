import console.ConsoleManager;
import collection.CollectionManager;

public class Application {
    public static void main(String[] args) {
        CollectionManager collectionManager = CollectionManager.getInstance();
        ConsoleManager consoleManager = ConsoleManager.getInstance(collectionManager);

        collectionManager.setConsoleManager(consoleManager);

        consoleManager.init();
    }
}
