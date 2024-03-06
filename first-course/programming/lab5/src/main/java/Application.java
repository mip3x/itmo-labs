import console.ConsoleManager;
import collection.CollectionManager;

public class Application {
    public static void main(String[] args) {
        CollectionManager collectionManager = CollectionManager.getInstance();
        ConsoleManager consoleManager = new ConsoleManager(collectionManager);

        consoleManager.init();
    }
}
