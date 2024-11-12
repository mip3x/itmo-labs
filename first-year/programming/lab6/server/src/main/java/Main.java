import io.console.InformationStorage;
import io.file.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Server;
import validation.CommandValidator;

public class Main {
    public static void main(String[] args) {
        Logger applicationLogger = LogManager.getLogger();
        applicationLogger.trace("Initializing server");

        if (args.length == 0) {
            applicationLogger.error("Not enough arguments: enter path to the file!");
            return;
        }

        try {
            FileManager.setFilePath(args[0]);

            InformationStorage.getInstance();
            CommandValidator.getInstance();

            FileManager.loadCollection();
        }
        catch (Exception exception) {
            applicationLogger.error(exception.getMessage());
            return;
        }

        applicationLogger.trace("ConsoleManager object created!");

        Server server = new Server(1337);
        server.init();
        server.run();
    }
}