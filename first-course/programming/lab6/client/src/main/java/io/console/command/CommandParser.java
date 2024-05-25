package io.console.command;

import java.util.Arrays;
import java.util.List;

public class CommandParser {
    public static CommandDTO parseCommand(String inputLine) {
        String[] tokens = inputLine.trim().split(" ");
        if (tokens[0].isBlank()) return null;

        return new CommandDTO(List.of(Arrays.copyOfRange(tokens, 0, tokens.length)));
    }
}
