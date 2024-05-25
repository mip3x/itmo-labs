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

    public record MatchedCommand(Command command, ValidationStatus validationStatus, String validationStatusDescription) {
    }

    public static CommandValidator getInstance() {
        if (instance == null) instance = new CommandValidator();
        return instance;
    }

    public CommandValidator() {
        setCommandArgumentMap();
        logger.trace("CommandArgumentMap was filled");
    }

    public static MatchedCommand validateCommand(CommandDTO commandDTO) {
        Command matchedCommand = InformationStorage.getCommandsList().stream()
                .filter(command -> command.getName().split(" ")[0].equals(commandDTO.commandName()))
                .findFirst()
                .orElse(null);

        if (matchedCommand == null)
            return new MatchedCommand(null, ValidationStatus.NOT_RECOGNIZED, "Command was not recognized!");

        List<Argument> requiredArguments = getArgumentsListByCommand(matchedCommand);

        if (commandDTO.commandArguments().size() < requiredArguments.size()) {
            if (requiredArguments.contains(Argument.ELEMENT) && (commandDTO.studyGroup() == null)) {

                if (requiredArguments.contains(Argument.ID)) {
                    String idHandlingMessage = handleIDValidation(matchedCommand);
                    if (idHandlingMessage != null) {
                        if (commandDTO.commandArguments().isEmpty())
                            return new MatchedCommand(matchedCommand, ValidationStatus.NOT_ENOUGH_ARGUMENTS, "not enough arguments!");
                        return new MatchedCommand(matchedCommand, ValidationStatus.INVALID_ID, idHandlingMessage);
                    }
                }
                return new MatchedCommand(matchedCommand, ValidationStatus.INPUT_REQUIRED, "input required!");
            }

            else if (commandDTO.studyGroup() != null) {
                logger.trace("STUDY GROUP != NULL");
                try {
                    commandDTO.studyGroup().validateStudyGroup();
                    return new MatchedCommand(matchedCommand, ValidationStatus.SUCCESS, null);
                } catch (Exception exception) {
                    return new MatchedCommand(matchedCommand, ValidationStatus.INVALID_OBJECT, exception.getMessage());
                }
            }

            return new MatchedCommand(matchedCommand, ValidationStatus.NOT_ENOUGH_ARGUMENTS, "not enough arguments!");

        } else if (commandDTO.commandArguments().size() > requiredArguments.size()) {
            return new MatchedCommand(matchedCommand, ValidationStatus.INVALID_ARGUMENTS, "invalid arguments!");
        }

        if (requiredArguments.contains(Argument.ID)) {
            String idHandlingMessage = handleIDValidation(matchedCommand);
            if (idHandlingMessage != null)
                return new MatchedCommand(matchedCommand, ValidationStatus.INVALID_ID, idHandlingMessage);
        }
        else if (requiredArguments.contains(Argument.ELEMENT)) {
            return new MatchedCommand(matchedCommand, ValidationStatus.INVALID_ARGUMENTS, "invalid arguments!");
        }

        return new MatchedCommand(matchedCommand, ValidationStatus.SUCCESS, null);
    }

    private static String handleIDValidation(Command matchedCommand) {
        try {
            if (!((RequestingId) matchedCommand).validateId()) {
                return "no object with such id!";
            }
        } catch (Exception exception) {
            return "error occurred while concatenating id!";
        }
        return null;
    }

    private static List<Argument> getArgumentsListByCommand(Command command) {
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
