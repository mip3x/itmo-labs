package transfer;

import collection.data.FormOfEducation;
import collection.data.StudyGroup;

import java.io.Serializable;

public class Data implements Serializable {
    private String id;
    private StudyGroup studyGroup;
    private FormOfEducation formOfEducation;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(FormOfEducation formOfEducation) {
        this.formOfEducation = formOfEducation;
    }
}
