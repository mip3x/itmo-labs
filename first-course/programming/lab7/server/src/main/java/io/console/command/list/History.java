package io.console.command.list;

import collection.CollectionService;
import io.console.InformationStorage;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

/**
 * Returns history of called commands
 */
public class History extends Command {
    private static final Logger historyCommandLogger = LogManager.getLogger();
    public History() {
        super("history", "Print out calls' history");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        String result = InformationStorage.getHistory().stream()
                .limit(InformationStorage.getHistorySize())
                .map(command -> String.format("%s%n", command.getName().split(" ")[0]))
                .collect(Collectors.joining());
        historyCommandLogger.trace("History command executed");
        if (!result.isEmpty()) return result.substring(0, result.length() - 1);
        return "History is empty";
    }
}
