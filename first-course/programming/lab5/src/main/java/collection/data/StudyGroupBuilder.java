package collection.data;

import java.util.Random;

public class StudyGroupBuilder {
    private final StudyGroup studyGroup;

    public StudyGroupBuilder() {
        this (new Random().nextInt(Integer.MAX_VALUE));
    }

    public StudyGroupBuilder(Integer id) {
        studyGroup = new StudyGroup();
        setId(id);
    }

    private void setId(Integer id) {
        studyGroup.setId(id);
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }
}
