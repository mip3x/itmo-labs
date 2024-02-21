import console.*;
import console.command.*;

public class Application {
    public static void main(String[] args) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        CommandValidator commandValidator = new CommandValidator();
        CommandManager commandManager = new CommandManager(commandValidator);
        ConsoleManager consoleManager = new ConsoleManager(consoleHandler, commandManager);

        consoleManager.init();
    }
}
