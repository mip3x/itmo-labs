package io.console.command;

import io.console.InformationStorage;
import io.console.command.list.Help;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Request;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandParser {
    private static Logger logger = LogManager.getLogger();
    public static Request parseCommand(String inputLine) {
        String[] tokens = inputLine.trim().split(" ");
        if (tokens[0].isBlank()) return null;

        Command matchedCommand = InformationStorage.getCommandsList().stream()
                .filter(command -> command.getName().split(" ")[0].equals(tokens[0]))
                .findFirst()
                .orElse(null);

        Request request = new Request(matchedCommand, Collections.emptyList());

        if (tokens.length > 1) request.setCommandArguments(List.copyOf(List.of(Arrays.copyOfRange(tokens, 1, tokens.length))));

        return request;
    }
}
