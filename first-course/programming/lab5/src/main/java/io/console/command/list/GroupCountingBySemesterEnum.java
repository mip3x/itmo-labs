package io.console.command.list;

import collection.CollectionManager;
import collection.data.Semester;
import collection.data.StudyGroup;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupCountingBySemesterEnum extends Command {
    public GroupCountingBySemesterEnum() {
        super("group_by_semester",
                "сгруппировать элементы по значению 'semesterEnum' и вывести количество элементов в каждой группе");
    }

    @Override
    public String execute() {
        LinkedList<StudyGroup> studyGroupCollection = CollectionManager.getInstance().getStudyGroupCollection();

        Map<Semester, Long> groupsPerSemester = studyGroupCollection.stream()
                .collect(Collectors.groupingBy(StudyGroup::getSemester, Collectors.counting()));

        StringBuilder result = new StringBuilder();
        groupsPerSemester.forEach((semester, count) ->
                result.append(MessageFormat.format("Семестр {0}: {1}\n", semester, count)));

        return result.substring(0, result.length() - 1);
    }
}
