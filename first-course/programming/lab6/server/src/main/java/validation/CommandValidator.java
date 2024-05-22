package validation;

import io.console.InformationStorage;
import io.console.command.Command;
import io.console.command.CommandDTO;
import io.console.command.list.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandValidator {
    private static final Logger logger = LogManager.getLogger();
    private static CommandValidator instance = null;
    private static HashMap<Command, List<Argument>> commandArgumentMap = new HashMap<>();

    public static CommandValidator getInstance() {
        if (instance == null) instance = new CommandValidator();
        return instance;
    }

    public CommandValidator() {
        setCommandArgumentMap();
        logger.trace("CommandArgumentMap was filled");
    }

    public static Command matchCommand(CommandDTO commandDTO) {
        return InformationStorage.getCommandsList().stream()
                .filter(command -> (new CommandDTO(command.getName().split(" ")[0])).equals(commandDTO))
                .findFirst()
                .orElse(null);
    }

    public static List<Argument> getArgumentsListByCommand(Command command) {
        return commandArgumentMap.entrySet().stream()
                .filter(commandListEntry -> command.getName().equals(commandListEntry.getKey().getName()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static HashMap<Command, List<Argument>> getCommandArgumentMap() {
        return commandArgumentMap;
    }

    private void setCommandArgumentMap() {
        commandArgumentMap.put(new Add(), List.of(Argument.ELEMENT));
        commandArgumentMap.put(new Update(), List.of(Argument.ID, Argument.ELEMENT));
        commandArgumentMap.put(new RemoveById(), List.of(Argument.ID));
        commandArgumentMap.put(new RemoveAnyByFormOfEducation(), List.of(Argument.FORM_OF_EDUCATION));
        commandArgumentMap.put(new Clear(), Collections.emptyList());
        commandArgumentMap.put(new Exit(), Collections.emptyList());
        commandArgumentMap.put(new Head(), Collections.emptyList());
        commandArgumentMap.put(new Help(), Collections.emptyList());
        commandArgumentMap.put(new History(), Collections.emptyList());
        commandArgumentMap.put(new Info(), Collections.emptyList());
        commandArgumentMap.put(new Show(), Collections.emptyList());
        commandArgumentMap.put(new Save(), Collections.emptyList());
        commandArgumentMap.put(new GroupCountingBySemesterEnum(), Collections.emptyList());
        commandArgumentMap.put(new PrintDescending(), Collections.emptyList());
        commandArgumentMap.put(new RemoveLower(), List.of(Argument.ELEMENT));
    }
}
