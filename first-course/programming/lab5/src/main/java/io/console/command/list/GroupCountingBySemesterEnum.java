package io.console.command.list;

import collection.CollectionManager;
import collection.data.Semester;
import collection.data.StudyGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Groups element of collection by semester
 */
public class GroupCountingBySemesterEnum extends Command {
    private static final Logger groupCountingBySemesterEnumCommandLogger = LogManager.getLogger();
    public GroupCountingBySemesterEnum() {
        super("group_by_semester",
                "сгруппировать элементы по значению 'semesterEnum' и вывести количество элементов в каждой группе");
    }

    @Override
    public String execute() {
        LinkedList<StudyGroup> studyGroupCollection = CollectionManager.getInstance().getStudyGroupCollection();
        groupCountingBySemesterEnumCommandLogger.trace("StudyGroupCollection has been got");

        Map<Semester, Long> groupsPerSemester = studyGroupCollection.stream()
                .collect(Collectors.groupingBy(StudyGroup::getSemester, Collectors.counting()));
        groupCountingBySemesterEnumCommandLogger.trace("Groups mapped");

        StringBuilder result = new StringBuilder();
        groupsPerSemester.forEach((semester, count) ->
                result.append(MessageFormat.format("Семестр {0}: {1}\n", semester, count)));

        groupCountingBySemesterEnumCommandLogger.trace("GroupCountingBySemesterEnum command executed");
        return result.substring(0, result.length() - 1);
    }
}
