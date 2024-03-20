package io.console.command.list;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.file.FileManager;

import java.util.LinkedList;

public class Save extends Command {
    public Save() {
        super("save", "сохраненить коллекцию в файл");
    }

    @Override
    public String execute() {
        LinkedList<StudyGroup> studyGroupCollection;
        if ((studyGroupCollection = CollectionManager.getInstance().getStudyGroupCollection()) == null)
            return "Коллекция пуста! Сохранение не произведено";
        FileManager.saveCollection(studyGroupCollection);
        return "Коллекция была успешно сохранена!";
    }
}
