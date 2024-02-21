import console.*;

public class Application {
    public static void main(String[] args) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        ConsoleManager consoleManager = new ConsoleManager(consoleHandler);

        consoleManager.init();
    }
}
