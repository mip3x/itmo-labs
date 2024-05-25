package io.console.command.list;

import collection.CollectionManager;
import io.console.InformationStorage;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

/**
 * Returns information about commands
 */
public class Help extends Command {
    private static final Logger helpCommandLogger = LogManager.getLogger();
    public Help() {
        super("help", "Print out the list of all commands");
    }

    @Override
    public String execute(CollectionManager collectionManager) {
        String result = InformationStorage.getCommandsList().stream()
                .map(command -> String.format("    %-30s%-4s%s%n", command.getName(), "->", command.getDescription()))
                .collect(Collectors.joining());
        helpCommandLogger.trace("Help command executed");
        return result.substring(0, result.length() - 1);
    }
}