package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import exception.InvalidInputException;
import io.console.InformationStorage;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Updates element with given id
 */
public class Update extends Command implements RequestingId, RequestingInput {
    private static final Logger updateCommandLogger = LogManager.getLogger();
    public Update() {
        super("update id {element}", "Update object by given id");
    }

    @Override
    public String execute(CollectionManager collectionManager) {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        Integer id = Integer.valueOf(InformationStorage.getReceivedArguments().get(0));

        updateCommandLogger.trace("Update command executed");
        return collectionManager.updateById(id, studyGroup);
    }

    @Override
    public boolean validateId(CollectionManager collectionManager) {
        try {
            updateCommandLogger.trace("Validating id");
            return collectionManager.validateID(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            updateCommandLogger.error("Enter valid id!");
            throw new InvalidInputException("Enter valid id!");
        }
    }
}
