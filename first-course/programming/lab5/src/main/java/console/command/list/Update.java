package console.command.list;

import console.InformationStorage;
import exception.InvalidInputException;

public class Update extends Command implements RequestingId, RequestingInput {
    public Update() {
        super("update", "обновить объект по заданному id");
    }

    @Override
    public String execute() {
        return null;
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
