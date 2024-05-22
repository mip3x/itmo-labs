package validation;

import io.console.InformationStorage;
import io.console.command.Command;
import io.console.command.CommandDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandValidator {
    private static final Logger logger = LogManager.getLogger();
    public static Command matchCommand(CommandDTO commandDTO) {
        return InformationStorage.getCommandsList().stream()
                .filter(command -> (new CommandDTO(command.getName().split(" ")[0])).equals(commandDTO))
                .findFirst()
                .orElse(null);
    }
}
