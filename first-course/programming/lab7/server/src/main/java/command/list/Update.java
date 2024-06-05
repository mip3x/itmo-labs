package command.list;

import collection.CollectionService;
import collection.data.StudyGroup;
import command.Command;
import exception.InvalidInputException;
import command.InformationStorage;
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
    public String execute(CollectionService collectionService, String username) {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        Integer id = Integer.valueOf(InformationStorage.getReceivedArguments().get(0));

        updateCommandLogger.trace("Update command executed");
        return collectionService.updateById(id, studyGroup);
    }

    @Override
    public boolean validateId(CollectionService collectionService) {
        try {
            updateCommandLogger.trace("Validating id");
            return collectionService.validateId(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            updateCommandLogger.error("Enter valid id!");
            throw new InvalidInputException("Enter valid id!");
        }
    }
}
