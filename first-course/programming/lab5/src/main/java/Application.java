import io.console.ConsoleManager;
import io.file.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class
 */
public class Application {
    /**
     * Main method to start the program
     * @param args Arguments from command line
     */
    public static void main(String[] args) {
        Logger applicationLogger = LogManager.getLogger();

        applicationLogger.trace("Entering method main()");

        if (args.length == 0) {
            applicationLogger.error("Недостаточно аргументов: введите путь к файлу!");
            return;
        }

        try {
            FileManager.setFilePath(args[0]);
        }
        catch (Exception exception) {
            applicationLogger.error(exception.getMessage());
            return;
        }

        ConsoleManager consoleManager = ConsoleManager.getInstance();
        applicationLogger.trace("Объект ConsoleManager создан");

        consoleManager.init();
    }
}
