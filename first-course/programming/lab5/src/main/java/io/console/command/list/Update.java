package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Updates element with given id
 */
public class Update extends Command implements RequestingId, RequestingInput {
    private static final Logger updateCommandLogger = LogManager.getLogger();
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Update() {
        super("update id {element}", "обновить объект по заданному id");
    }

    @Override
    public String execute() {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        Integer id = Integer.valueOf(InformationStorage.getReceivedArguments().get(0));

        updateCommandLogger.trace("Update command executed");
        return collectionManager.updateById(id, studyGroup);
    }

    @Override
    public boolean validateId() {
        try {
            updateCommandLogger.trace("Validating id");
            return collectionManager.validateID(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            updateCommandLogger.error("Enter valid id!");
            throw new InvalidInputException("Введите валидный id!");
        }
    }
}
