package collection.data;

import exception.InvalidInputException;

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
        setId(new Random().nextInt(Integer.MAX_VALUE) + 1);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean setName(String name) throws InvalidInputException {
        if (name == null || name.isBlank()) throw new InvalidInputException("Поле 'Имя' не должно быть пустым!");
        this.name = name;
        return true;
    }

    public boolean setCoordinates(Coordinates coordinates) throws InvalidInputException {
        if (coordinates == null) throw new InvalidInputException("Поле 'Координаты' не должно быть пустым!");
        this.coordinates = coordinates;
        return true;
    }

    public boolean setStudentsCount(Long studentsCount) throws InvalidInputException {
        if (studentsCount <= 0) throw new InvalidInputException("Количество студентов должно быть больше нуля!");
        this.studentsCount = studentsCount;
        return true;
    }

    public boolean setShouldBeExpelled(Long shouldBeExpelled) throws InvalidInputException {
        if (shouldBeExpelled <= 0) throw new InvalidInputException("Количество студентов на отчисление должно быть больше нуля!");
        this.shouldBeExpelled = shouldBeExpelled;
        return true;
    }

    public boolean setFormOfEducation(FormOfEducation formOfEducation) throws InvalidInputException {
        if (formOfEducation == null) throw new InvalidInputException("Поле 'Форма обучения' не должно быть пустым!");
        this.formOfEducation = formOfEducation;
        return true;
    }

    public boolean setSemesterEnum(Semester semesterEnum) throws InvalidInputException {
        if (semesterEnum == null) throw new InvalidInputException("Поле 'Семестр' не должно быть пустым!");
        this.semesterEnum = semesterEnum;
        return true;
    }

    public boolean setGroupAdmin(Person groupAdmin) throws InvalidInputException {
        if (groupAdmin == null) throw new InvalidInputException("Поле 'Староста группы' не должно быть пустым!");
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
//        String studyGroupCoordinates = "Координаты: \n" + coordinates.toString();
//        String studyGroupCreationDate = "Дата создания: " + creationDate;
        String studyGroupStudentsCount = "Количество студентов: " + studentsCount;
//        String studyGroupShouldBeExpelled = "Количество студентов, которых нужно исключить: " + shouldBeExpelled;
//        String studyGroupFormOfEducation = "Форма обучения: " + formOfEducation;
//        String studyGroupSemester = "Семестр обучения: " + semesterEnum;
//        String studyGroupPerson = "Староста группы: \n" + groupAdmin.toString();

        return studyGroupName + "\n"
             + studyGroupID + "\n"
//             + studyGroupCoordinates + "\n"
//             + studyGroupCreationDate + "\n"
             + studyGroupStudentsCount;
//             + studyGroupShouldBeExpelled + "\n"
//             + studyGroupFormOfEducation + "\n"
//             + studyGroupSemester + "\n"
//             + studyGroupPerson + "\n";
    }
}
