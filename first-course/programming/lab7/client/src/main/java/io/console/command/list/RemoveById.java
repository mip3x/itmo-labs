package io.console.command.list;

import collection.CollectionService;
import exception.InvalidInputException;
import io.console.InformationStorage;
import io.console.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Removes element with given id
 */
public class RemoveById extends Command implements RequestingId {
    private static final Logger removeByIdCommandLogger = LogManager.getLogger();
    public RemoveById() {
        super("remove_by_id id", "Delete the object by given id");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        removeByIdCommandLogger.trace("RemoveById command executed");
        return collectionService.removeById(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
    }

    @Override
    public boolean validateId(CollectionService collectionService) {
        try {
            removeByIdCommandLogger.trace("Trying validate id");
            return collectionService.validateID(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            removeByIdCommandLogger.error("Enter valid id!");
            throw new InvalidInputException("Enter valid id!");
        }
    }
}
