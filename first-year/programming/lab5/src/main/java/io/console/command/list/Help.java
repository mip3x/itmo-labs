package io.console.command.list;

import io.console.InformationStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

/**
 * Returns information about commands
 */
public class Help extends Command {
    private static final Logger helpCommandLogger = LogManager.getLogger();
    public Help() {
        super("help", "Вывести список всех команд");
    }

    @Override
    public String execute() {
        String result = InformationStorage.getCommandsList().stream()
                .map(command -> String.format("    %-30s%-4s%s%n", command.getName(), "->", command.getDescription()))
                .collect(Collectors.joining());
        helpCommandLogger.trace("Help command executed");
        return result.substring(0, result.length() - 1);
    }
}