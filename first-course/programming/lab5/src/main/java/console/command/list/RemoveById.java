package console.command.list;

import console.InformationStorage;
import exception.InvalidInputException;

public class RemoveById extends Command implements RequestingId {
    public RemoveById() {
        super("remove_by_id", "удалить объект по заданному id");
    }

    @Override
    public String execute() {
        return InformationStorage.getCollectionManager().removeById(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
    }

    @Override
    public boolean validateId() {
        try {
            return InformationStorage.getCollectionManager().validateID(Integer.valueOf(InformationStorage.getReceivedArguments().get(0)));
        }
        catch (Exception exception) {
            throw new InvalidInputException("Введите валидный id!");
        }
    }
}
