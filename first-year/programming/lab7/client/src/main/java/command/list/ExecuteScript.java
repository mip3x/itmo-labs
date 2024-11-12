package command.list;

import collection.CollectionService;
import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Executes script
 */
public class ExecuteScript extends Command {
    private static final Logger executeScriptCommandLogger = LogManager.getLogger();
    public ExecuteScript() {
        super("execute_script file_name", "Read and execute script from the given file");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        return null;
    }
}
