package validation;

import io.console.InformationStorage;
import io.console.command.Command;
import io.console.command.CommandDTO;

public class CommandValidator {
    public static Command matchCommand(CommandDTO commandDTO) {
        return InformationStorage.getCommandsList().stream()
                .filter(command -> (new CommandDTO(command.getName())).equals(commandDTO))
                .findFirst()
                .orElse(null);
    }
}
