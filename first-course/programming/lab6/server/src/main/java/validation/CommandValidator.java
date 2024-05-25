package validation;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import io.console.command.Command;
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

    public static MatchedCommand validateCommand(Command providedCommand, List<String> providedArguments, StudyGroup providedStudyGroup) {
        if (providedCommand == null)
            return new MatchedCommand(null, ValidationStatus.NOT_RECOGNIZED, "Command was not recognized!");

        List<Argument> requiredArguments = getArgumentsListByCommand(providedCommand);

        if (providedArguments.size() < requiredArguments.size()) {
            if (requiredArguments.contains(Argument.ELEMENT) && (providedStudyGroup == null)) {

                if (requiredArguments.contains(Argument.ID)) {
                    String idHandlingMessage = handleIDValidation(providedCommand);
                    if (idHandlingMessage != null) {
                        if (providedArguments.isEmpty())
                            return new MatchedCommand(providedCommand, ValidationStatus.NOT_ENOUGH_ARGUMENTS, "not enough arguments!");
                        return new MatchedCommand(providedCommand, ValidationStatus.INVALID_ID, idHandlingMessage);
                    }
                }
                return new MatchedCommand(providedCommand, ValidationStatus.INPUT_REQUIRED, "input required!");
            }

            else if (providedStudyGroup != null) {
                logger.trace("STUDY GROUP != NULL");
                try {
                    providedStudyGroup.validateStudyGroup();
                    return new MatchedCommand(providedCommand, ValidationStatus.SUCCESS, null);
                } catch (Exception exception) {
                    return new MatchedCommand(providedCommand, ValidationStatus.INVALID_OBJECT, exception.getMessage());
                }
            }

            return new MatchedCommand(providedCommand, ValidationStatus.NOT_ENOUGH_ARGUMENTS, "not enough arguments!");

        } else if (providedArguments.size() > requiredArguments.size()) {
            return new MatchedCommand(providedCommand, ValidationStatus.INVALID_ARGUMENTS, "invalid arguments!");
        }

        if (requiredArguments.contains(Argument.ID)) {
            String idHandlingMessage = handleIDValidation(providedCommand);
            if (idHandlingMessage != null)
                return new MatchedCommand(providedCommand, ValidationStatus.INVALID_ID, idHandlingMessage);
        }
        else if (requiredArguments.contains(Argument.ELEMENT)) {
            return new MatchedCommand(providedCommand, ValidationStatus.INVALID_ARGUMENTS, "invalid arguments!");
        }

        return new MatchedCommand(providedCommand, ValidationStatus.SUCCESS, null);
    }

    private static String handleIDValidation(Command matchedCommand) {
        try {
            if (!((RequestingId) matchedCommand).validateId(CollectionManager.getInstance())) {
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
