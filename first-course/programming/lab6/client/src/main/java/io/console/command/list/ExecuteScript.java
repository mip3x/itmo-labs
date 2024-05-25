package io.console.command.list;

import io.console.command.Command;
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
    public String execute() {
        return "Ability of execution script on the server was deleted!";
    }
}
