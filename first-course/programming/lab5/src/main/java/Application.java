import io.console.ConsoleManager;
import collection.CollectionManager;
import io.console.InformationStorage;

public class Application {
    public static void main(String[] args) {
        ConsoleManager consoleManager = ConsoleManager.getInstance();

        consoleManager.init();
    }
}
