import io.console.ConsoleManager;
import io.file.FileManager;

/**
 * Main class
 */
public class Application {
    /**
     * Main method to start the program
     * @param args Arguments from command line
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Недостаточно аргументов: введите путь к файлу!");
            return;
        }

        try {
            FileManager.setFilePath(args[0]);
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
            return;
        }

        ConsoleManager consoleManager = ConsoleManager.getInstance();

        consoleManager.init();
    }
}
