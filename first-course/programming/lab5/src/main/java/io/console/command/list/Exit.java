package io.console.command.list;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exits from program
 */
public class Exit extends Command {
    private static final Logger exitCommandLogger = LogManager.getLogger();
    public Exit() {
        super("exit", "Выйти из программы");
    }

    @Override
    public String execute() {
        exitCommandLogger.info("Exiting program");
        System.exit(0);
        return "Выход из программы";
    }
}
