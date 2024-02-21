import console.*;
import console.command.*;

public class Application {
    public static void main(String[] args) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        CommandManager commandManager = new CommandManager(consoleHandler);
        ConsoleManager consoleManager = new ConsoleManager(consoleHandler, commandManager);

        consoleManager.init();
    }
}
