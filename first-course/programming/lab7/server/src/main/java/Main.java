import io.console.ConsoleHandler;
import io.console.ConsoleManager;
import io.console.InformationStorage;
import io.database.DBManager;
import io.file.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Server;
import validation.CommandValidator;

import java.io.FileReader;
import java.util.Properties;

public class Main {
    private static final String CONFIG_FILE_NAME = "server/src/main/resources/config.properties";
    public static void main(String[] args) {
        Logger applicationLogger = LogManager.getLogger();
        applicationLogger.trace("Initializing server");

        Properties dbConnectionArguments = new Properties();

        try (FileReader fileReader = new FileReader(CONFIG_FILE_NAME)) {
            dbConnectionArguments.load(fileReader);

            String dbUrl = dbConnectionArguments.getProperty("DB.URL");
            String dbUser = dbConnectionArguments.getProperty("DB.USER");
            String dbPassword = dbConnectionArguments.getProperty("DB.PASSWORD");

            if (!DBManager.getInstance().establishConnection(dbUrl, dbUser, dbPassword))
                return;

            FileManager.setFilePath("server/collection.xml");
            FileManager.loadCollection();

            InformationStorage.getInstance();
            CommandValidator.getInstance();
        }
        catch (Exception exception) {
            applicationLogger.error(exception.getMessage());
            return;
        }

        applicationLogger.trace("ConsoleManager object created!");

        Server server = new Server(1337);
        Thread serverThread = new Thread(server, "SERVER");
        server.init();
        serverThread.start();

        ConsoleManager serverConsole = new ConsoleManager(new ConsoleHandler());
        Thread serverConsoleThread = new Thread(serverConsole, "CONSOLE");
        serverConsoleThread.start();
    }
}