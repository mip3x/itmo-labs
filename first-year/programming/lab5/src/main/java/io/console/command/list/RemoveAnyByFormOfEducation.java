package io.console.command.list;

import collection.CollectionManager;
import collection.data.FormOfEducation;
import collection.data.StudyGroup;
import io.console.InformationStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Removes objects with given formOfEducation
 */
public class RemoveAnyByFormOfEducation extends Command {
    private final static Logger removeAnyByFormOfEducationLogger = LogManager.getLogger();
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
            removeAnyByFormOfEducationLogger.trace("FormOfEducation has been got");
        }
        catch (Exception exception) {
            removeAnyByFormOfEducationLogger.error("Enter valid value for formOfEducation field!");
            return MessageFormat.
                    format( "Введите валидное значение поля formOfEducation!\n{0}",
                            Arrays.toString(FormOfEducation.values()));
        }

        LinkedList<StudyGroup> studyGroupCollection = CollectionManager.getInstance().getStudyGroupCollection();

        removeAnyByFormOfEducationLogger.trace("RemoveAnyByFormOfEducation command executed");
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
