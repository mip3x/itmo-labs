package command.list;

import collection.CollectionService;
import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Returns info about collection
 */
public class Info extends Command {
    private static final Logger infoCommandLogger = LogManager.getLogger();
    public Info() {
        super("info", "Print out info about collection");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        infoCommandLogger.trace("Info command executed");
        return collectionService.getCollectionInfo();
    }
}
