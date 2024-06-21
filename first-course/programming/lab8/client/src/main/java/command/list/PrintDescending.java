package command.list;

import collection.CollectionService;
import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

/**
 * Returns elements of collection from high to low
 */
public class PrintDescending extends Command {
    private static final Logger printDescendingLogger = LogManager.getLogger();
    public PrintDescending() {
        super("print_descending", "Print elements of collection by descending");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        Collections.sort(collectionService.getCollection());
        printDescendingLogger.trace("Collection sorted");

        Collections.reverse(collectionService.getCollection());
        printDescendingLogger.trace("Collection reversed");

        printDescendingLogger.trace("PrintDescending command executed");
        return collectionService.getAllStudyGroupsInfo();
    }
}
