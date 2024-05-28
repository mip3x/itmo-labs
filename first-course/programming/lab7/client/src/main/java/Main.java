import io.console.ConsoleHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;

/**
 * Main class
 */
public class Main {
    /**
     * Main method to start the program
     * @param args Arguments from command line
     */
    public static void main(String[] args) {
        Logger applicationLogger = LogManager.getLogger();
        applicationLogger.trace("Initializing client");

        ConsoleHandler consoleHandler = new ConsoleHandler();
        Client client = new Client("localhost", 1337, consoleHandler);
        client.run();
    }
}
