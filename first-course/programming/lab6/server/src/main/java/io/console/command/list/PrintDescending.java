package io.console.command.list;

import collection.CollectionManager;
import io.console.command.Command;
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
    public String execute(CollectionManager collectionManager) {
        Collections.sort(collectionManager.getStudyGroupCollection());
        printDescendingLogger.trace("Collection sorted");

        Collections.reverse(collectionManager.getStudyGroupCollection());
        printDescendingLogger.trace("Collection reversed");

        printDescendingLogger.trace("PrintDescending command executed");
        return collectionManager.getAllStudyGroupsInfo();
    }
}
