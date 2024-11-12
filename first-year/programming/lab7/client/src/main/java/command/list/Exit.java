package command.list;

import collection.CollectionService;
import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exits from program
 */
public class Exit extends Command {
    private static final Logger exitCommandLogger = LogManager.getLogger();
    public Exit() {
        super("exit", "Exit from the program");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        exitCommandLogger.info("Exiting program");
        System.exit(0);
        return "Exiting program";
    }
}