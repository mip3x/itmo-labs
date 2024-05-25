package io.console.command.list;

import collection.CollectionManager;
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
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public RemoveById() {
        super("remove_by_id id", "Delete the object by given id");
    }

    @Override
    public String execute() {
        removeByIdCommandLogger.trace("RemoveById command executed");
        return collectionManager.removeById(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
    }

    @Override
    public boolean validateId() {
        try {
            removeByIdCommandLogger.trace("Trying validate id");
            return collectionManager.validateID(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            removeByIdCommandLogger.error("Enter valid id!");
            throw new InvalidInputException("Enter valid id!");
        }
    }
}
