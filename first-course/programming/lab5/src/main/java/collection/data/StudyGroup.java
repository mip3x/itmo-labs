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
        Integer id = new Random().nextInt(Integer.MAX_VALUE);
    }

    // public void validateData()
    //
    // public void setId(Integer id) {
    //     this.id = id;
    // }
    //
    // public void setName(String name) {
    //     if (name.length > 0) {
    //         this.name = name;
    //     }
    // }
    //
    // public void setCoordinates(Coordinates coordinates) {
    //
    // }

    @Override
    public int compareTo(StudyGroup that) {
        if (this.studentsCount > that.studentsCount) return 1;
        else return -1;
    }
}
