package io.console.command.list;

import collection.CollectionManager;
import collection.data.FormOfEducation;
import collection.data.StudyGroup;
import io.console.InformationStorage;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;

public class RemoveAnyByFormOfEducation extends Command {
    public RemoveAnyByFormOfEducation() {
        super("remove_by_FOE formOfEduction",
                MessageFormat.format("удалить из коллекции один элемент," +
                        " значение поля formOfEducation которого эквивалентно заданному. " +
                        "Значения formOfEducation: {0}", Arrays.toString(FormOfEducation.values())));
    }

    @Override
    public String execute() {
        FormOfEducation formOfEducation;
        try {
            formOfEducation = FormOfEducation
                    .valueOf(InformationStorage.getReceivedArguments().get(0));
        }
        catch (Exception exception) {
            return MessageFormat.
                    format( "Введите валидное значение поля formOfEducation!\n{0}",
                            Arrays.toString(FormOfEducation.values()));
        }

        LinkedList<StudyGroup> studyGroupCollection = CollectionManager.getInstance().getStudyGroupCollection();
        return studyGroupCollection
                .stream()
                .filter(studyGroup -> studyGroup.getFormOfEducation().equals(formOfEducation))
                .findAny()
                .map(studyGroup -> {
                    studyGroupCollection.remove(studyGroup);
                    return "Объект успешно удален";
                })
                .orElse("Объект с таким же значением поля formOfEducation не найден!");
    }
}
