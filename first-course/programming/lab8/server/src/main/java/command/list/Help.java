package command.list;

import collection.CollectionService;
import command.InformationStorage;
import command.Command;
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
    public String execute(CollectionService collectionService, String username) {
        String result = InformationStorage.getCommandsList().stream()
                .map(command -> String.format("    %-30s%-4s%s%n", command.getName(), "->", command.getDescription()))
                .collect(Collectors.joining());
        helpCommandLogger.trace("Help command executed");
        return result.substring(0, result.length() - 1);
    }
}