package io.console.command;

import java.util.Arrays;
import java.util.List;

public class CommandParser {
    public static CommandDTO parseCommand(String inputLine) {
        String[] tokens = inputLine.trim().split(" ");
        if (tokens[0].isBlank()) return null;

        if (tokens.length > 1) {
            return new CommandDTO(tokens[0],
                    List.of(Arrays.copyOfRange(tokens, 1, tokens.length)));
        } else {
            return new CommandDTO(tokens[0]);
        }
    }
}
