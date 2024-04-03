package io.console.command.list;

import collection.CollectionManager;
import io.console.InformationStorage;
import exception.InvalidInputException;

/**
 * Removes element with given id
 */
public class RemoveById extends Command implements RequestingId {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public RemoveById() {
        super("remove_by_id id", "удалить объект по заданному id");
    }

    @Override
    public String execute() {
        return collectionManager.removeById(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
    }

    @Override
    public boolean validateId() {
        try {
            return collectionManager.validateID(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            throw new InvalidInputException("Введите валидный id!");
        }
    }
}
