package collection.data;

import exception.InvalidInputException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.Random;

@XmlRootElement(name = "studyGroup")
public class StudyGroup implements Comparable<StudyGroup> {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long studentsCount = null; //Значение поля должно быть больше 0, Поле может быть null
    private long shouldBeExpelled; //Значение поля должно быть больше 0
    private FormOfEducation formOfEducation; //Поле не может быть null
    private Semester semester; //Поле не может быть null
    private Person groupAdmin; //Поле не может быть null

    public StudyGroup() {
        creationDate = new Date();
        setId(new Random().nextInt(Integer.MAX_VALUE) + 1);
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @XmlElement
    public Integer getId() {
        return id;
    }

    public void setDate(Date date) {
        creationDate = date;
    }

    @XmlElement
    public Date getCreationDate() {
        return creationDate;
    }

    public boolean compareId(Integer id) {
        return this.id.equals(id);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new InvalidInputException("Поле 'Имя' не должно быть пустым!");
        this.name = name;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new InvalidInputException("Поле 'Координаты' не должно быть пустым!");
        this.coordinates = coordinates;
    }

    @XmlElement
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setStudentsCount(Long studentsCount) {
        if (studentsCount == null) return;
        if (studentsCount <= 0) throw new InvalidInputException("Количество студентов должно быть больше нуля!");
        this.studentsCount = studentsCount;
    }

    @XmlElement
    public Long getStudentsCount() {
        return studentsCount;
    }

    public void setShouldBeExpelled(Long shouldBeExpelled) {
        if (shouldBeExpelled == null) throw new InvalidInputException("Поле 'кол-во студентов на отчисление' не должно быть пустым!");
        if (shouldBeExpelled <= 0) throw new InvalidInputException("Количество студентов на отчисление должно быть больше нуля!");
        this.shouldBeExpelled = shouldBeExpelled;
    }

    @XmlElement
    public Long getShouldBeExpelled() {
        return shouldBeExpelled;
    }

    public void setFormOfEducation(FormOfEducation formOfEducation) {
        if (formOfEducation == null) throw new InvalidInputException("Поле 'Форма обучения' не должно быть пустым!");
        this.formOfEducation = formOfEducation;
    }

    @XmlElement
    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public void setSemester(Semester semester) {
        if (semester == null) throw new InvalidInputException("Поле 'Семестр' не должно быть пустым!");
        this.semester = semester;
    }

    @XmlElement
    public Semester getSemester() {
        return semester;
    }

    public void setGroupAdmin(Person groupAdmin) {
        if (groupAdmin == null) throw new InvalidInputException("Поле 'Староста группы' не должно быть пустым!");
        this.groupAdmin = groupAdmin;
    }

    @XmlElement
    public Person getGroupAdmin() {
        return groupAdmin;
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
        String studyGroupCoordinates = "Координаты: " + coordinates.toString();
        String studyGroupCreationDate = "Дата создания: " + creationDate;
        String studyGroupStudentsCount = "Количество студентов: " + studentsCount;
        String studyGroupShouldBeExpelled = "Количество студентов, которых нужно исключить: " + shouldBeExpelled;
        String studyGroupFormOfEducation = "Форма обучения: " + formOfEducation;
        String studyGroupSemester = "Семестр обучения: " + semester;
        String studyGroupAdmin = "Староста группы: \n" + groupAdmin.toString();

        return studyGroupName + "\n"
             + studyGroupID + "\n"
             + studyGroupCoordinates + "\n"
             + studyGroupCreationDate + "\n"
             + studyGroupStudentsCount + "\n"
             + studyGroupShouldBeExpelled + "\n"
             + studyGroupFormOfEducation + "\n"
             + studyGroupSemester + "\n"
             + studyGroupAdmin;
    }
}