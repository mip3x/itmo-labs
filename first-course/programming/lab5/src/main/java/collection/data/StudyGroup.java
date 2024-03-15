package collection.data;

import java.util.Date;
import java.util.Random;

public class StudyGroup implements Comparable<StudyGroup> {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long studentsCount; //Значение поля должно быть больше 0, Поле может быть null
    private long shouldBeExpelled; //Значение поля должно быть больше 0
    private FormOfEducation formOfEducation; //Поле не может быть null
    private Semester semesterEnum; //Поле не может быть null
    private Person groupAdmin; //Поле не может быть null

    public StudyGroup() {
        creationDate = new Date();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean setName(String name) {
        if (name == null || name.isEmpty()) return false;
        this.name = name;
        return true;
    }

    public boolean setCoordinates(Coordinates coordinates) {
        if (coordinates == null) return false;
        this.coordinates = coordinates;
        return true;
    }

    public boolean setStudentsCount(Long studentsCount) {
        if (studentsCount <= 0) return false;
        this.studentsCount = studentsCount;
        return true;
    }

    public boolean setShouldBeExpelled(Long shouldBeExpelled) {
        if (shouldBeExpelled <= 0) return false;
        this.shouldBeExpelled = shouldBeExpelled;
        return true;
    }

    public boolean setFormOfEducation(FormOfEducation formOfEducation) {
        if (formOfEducation == null) return false;
        this.formOfEducation = formOfEducation;
        return true;
    }

    public boolean setSemesterEnum(Semester semesterEnum) {
        if (semesterEnum == null) return false;
        this.semesterEnum = semesterEnum;
        return true;
    }

    public boolean setGroupAdmin(Person groupAdmin) {
        if (groupAdmin == null) return false;
        this.groupAdmin = groupAdmin;
        return true;
    }

    @Override
    public int compareTo(StudyGroup that) {
        if (this.studentsCount > that.studentsCount) return 1;
        else return -1;
    }

    @Override
    public String toString() {
        String studyGroupName = "Учебная группа " + name;
        String studyGroupID = "id: " + id;
        String studyGroupCoordinates = "Координаты: \n" + coordinates.toString();
        String studyGroupCreationDate = "Дата создания: " + creationDate;
        String studyGroupStudentsCount = "Количество студентов: " + studentsCount;
        String studyGroupShouldBeExpelled = "Количество студентов, которых нужно исключить: " + shouldBeExpelled;
        String studyGroupFormOfEducation = "Форма обучения: " + formOfEducation;
        String studyGroupSemester = "Семестр обучения: " + semesterEnum;
        String studyGroupPerson = "Староста группы: \n" + groupAdmin.toString();

        return studyGroupName + "\n"
             + studyGroupID + "\n"
             + studyGroupCoordinates + "\n"
             + studyGroupCreationDate + "\n"
             + studyGroupStudentsCount + "\n"
             + studyGroupShouldBeExpelled + "\n"
             + studyGroupFormOfEducation + "\n"
             + studyGroupSemester + "\n"
             + studyGroupPerson + "\n";
    }
}
