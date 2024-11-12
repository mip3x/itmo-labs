package command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dto.CommandDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandParser {
    private static Logger logger = LogManager.getLogger();
    public static CommandDto parseCommand(String inputLine) {
        String[] tokens = inputLine.trim().split(" ");
        if (tokens[0].isBlank()) return null;

        Command matchedCommand = InformationStorage.getCommandsList().stream()
                .filter(command -> command.getName().split(" ")[0].equals(tokens[0]))
                .findFirst()
                .orElse(null);

        CommandDto commandDto = new CommandDto(matchedCommand, Collections.emptyList());

        if (tokens.length > 1) commandDto.setCommandArguments(List.copyOf(List.of(Arrays.copyOfRange(tokens, 1, tokens.length))));

        return commandDto;
    }
}
