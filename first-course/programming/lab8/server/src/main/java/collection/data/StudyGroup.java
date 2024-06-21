package collection.data;

import exception.InvalidInputException;

import java.io.Serializable;
import java.util.Date;

/**
 * Study group - class of elements of collection
 */
public class StudyGroup implements Comparable<StudyGroup>, Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long studentsCount = null; //Значение поля должно быть больше 0, Поле может быть null
    private long shouldBeExpelled; //Значение поля должно быть больше 0
    private FormOfEducation formOfEducation; //Поле не может быть null
    private Semester semester; //Поле не может быть null
    private Person groupAdmin; //Поле не может быть null
    private String creator;

    public StudyGroup() {
        creationDate = new Date();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCreationDate(Date date) {
        creationDate = date;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean compareId(Integer id) {
        return this.id.equals(id);
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) {
//        if (name == null || name.isBlank()) throw new InvalidInputException("Поле 'Имя' не должно быть пустым!");
        if (name == null || name.isBlank()) throw new InvalidInputException("Field 'Name' should not be empty!");
    }

    public String getName() {
        return name;
    }

    public void setCoordinates(Coordinates coordinates) {
        validateCoordinates(coordinates);
        this.coordinates = coordinates;
    }

    private void validateCoordinates(Coordinates coordinates) {
//        if (coordinates == null) throw new InvalidInputException("Поле 'Координаты' не должно быть пустым!");
        if (coordinates == null) throw new InvalidInputException("Field 'Coordinates' should not be empty!");
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setStudentsCount(Long studentsCount) {
        validateStudentsCount(studentsCount);
        this.studentsCount = studentsCount;
    }

    private void validateStudentsCount(Long studentsCount) {
        if (studentsCount == null) throw new InvalidInputException("Value of field 'Quantity of students' should not be empty!");
//        if (studentsCount <= 0) throw new InvalidInputException("Значение поля 'Количество студентов' должно быть больше нуля!");
        if (studentsCount <= 0) throw new InvalidInputException("Value of field 'Quantity of students' should be greater than 0!");
    }

    public Long getStudentsCount() {
        return studentsCount;
    }

    public void setShouldBeExpelled(Long shouldBeExpelled) {
        validateShouldBeExpelled(shouldBeExpelled);
        this.shouldBeExpelled = shouldBeExpelled;
    }

    private void validateShouldBeExpelled(Long shouldBeExpelled) {
//        if (shouldBeExpelled == null) throw new InvalidInputException("Поле 'Количество студентов на отчисление' не должно быть пустым!");
        if (shouldBeExpelled == null) throw new InvalidInputException("Field 'Should be expelled' should not be empty!");
//        if (shouldBeExpelled <= 0) throw new InvalidInputException("Значение поля 'Количество студентов на отчисление' должно быть больше нуля!");
        if (shouldBeExpelled <= 0) throw new InvalidInputException("Value of field 'Should be expelled' should be greater than 0!");
    }

    public Long getShouldBeExpelled() {
        return shouldBeExpelled;
    }

    public void setFormOfEducation(FormOfEducation formOfEducation) {
        validateFormOfEducation(formOfEducation);
        this.formOfEducation = formOfEducation;
    }

    private void validateFormOfEducation(FormOfEducation formOfEducation) {
//        if (formOfEducation == null) throw new InvalidInputException("Поле 'Форма обучения' не должно быть пустым!");
        if (formOfEducation == null) throw new InvalidInputException("Field 'Form of education' should not be empty!");
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public void setSemester(Semester semester) {
        validateSemester(semester);
        this.semester = semester;
    }

    private void validateSemester(Semester semester) {
//        if (semester == null) throw new InvalidInputException("Поле 'Семестр' не должно быть пустым!");
        if (semester == null) throw new InvalidInputException("Field 'Semester' should not be empty!");
    }

    public Semester getSemester() {
        return semester;
    }

    public void setGroupAdmin(Person groupAdmin) {
        validateGroupAdmin(groupAdmin);
        this.groupAdmin = groupAdmin;
    }

    private void validateGroupAdmin(Person groupAdmin) {
//        if (groupAdmin == null) throw new InvalidInputException("Поле 'Староста группы' не должно быть пустым!");
        if (groupAdmin == null) throw new InvalidInputException("Field 'Group admin' should not be empty!");
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        validateCreator(creator);
        this.creator = creator;
    }

    public void validateCreator(String creator) {
        if (creator == null || creator.isBlank()) throw new InvalidInputException("Field 'Creator' should not be empty!");
    }

    public void validateStudyGroup() {
        this.validateName(this.getName());
        this.validateCoordinates(this.getCoordinates());
        this.validateStudentsCount(this.getStudentsCount());
        this.validateShouldBeExpelled(this.getShouldBeExpelled());
        this.validateFormOfEducation(this.getFormOfEducation());
        this.validateSemester(this.getSemester());
        this.validateGroupAdmin(this.getGroupAdmin());
        this.validateCreator(this.getCreator());
    }

    @Override
    public int compareTo(StudyGroup other) {
        return Long.compare(this.studentsCount, other.studentsCount);
    }

    @Override
    public String toString() {
//        String studyGroupName = "Учебная группа " + name;
        String studyGroupName = "Study group " + name;
        String studyGroupID = "id: " + id;
//        String studyGroupCoordinates = "Координаты: " + coordinates.toString();
        String studyGroupCoordinates = "Coordinates: " + coordinates.toString();
//        String studyGroupCreationDate = "Дата создания: " + creationDate;
        String studyGroupCreationDate = "Creation date: " + creationDate;
//        String studyGroupStudentsCount = "Количество студентов: " + studentsCount;
        String studyGroupStudentsCount = "Quantity of students: " + studentsCount;
//        String studyGroupShouldBeExpelled = "Количество студентов, которых нужно исключить: " + shouldBeExpelled;
        String studyGroupShouldBeExpelled = "Quantity of students that should be expelled: " + shouldBeExpelled;
//        String studyGroupFormOfEducation = "Форма обучения: " + formOfEducation;
        String studyGroupFormOfEducation = "Form of education: " + formOfEducation;
//        String studyGroupSemester = "Семестр обучения: " + semester;
        String studyGroupSemester = "Study semester: " + semester;
//        String studyGroupAdmin = "Староста группы: \n" + groupAdmin.toString();
        String studyGroupAdmin = "Group admin: \n" + groupAdmin.toString();

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