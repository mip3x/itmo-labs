package collection.data;

import java.util.Random;

public class StudyGroupBuilder {
    private final StudyGroup studyGroup;

    public StudyGroupBuilder() {
        Integer id = new Random().nextInt(Integer.MAX_VALUE);
        this(id);
    }

    public StudyGroupBuilder(Integer id) {
        studyGroup = new StudyGroup();
    }

    private void setId(Integer id) {
        studyGroup.setId(id);
    }
}
