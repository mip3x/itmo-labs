package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.file.FileManager;

import java.util.LinkedList;

public class Save extends Command {
    public Save() {
        super("save", "сохранить коллекцию в файл");
    }

    @Override
    public String execute() {
        if (CollectionManager.getInstance().getStudyGroupCollection() == null)
            return "Коллекция пуста! Сохранение не произведено!";
        try {
            FileManager.saveCollection();
            return "Коллекция была успешно сохранена!";
        }
        catch (Exception exception) {
            return exception.getMessage();
        }
    }
}
