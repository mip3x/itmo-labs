package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import exception.InvalidInputException;

public class Update extends Command implements RequestingId, RequestingInput {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public Update() {
        super("update id {element}", "обновить объект по заданному id");
    }

    @Override
    public String execute() {
        StudyGroup studyGroup = InformationStorage.getReceivedStudyGroup();
        Integer id = Integer.valueOf(InformationStorage.getReceivedArguments().get(0));
        return collectionManager.updateById(id, studyGroup);
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
