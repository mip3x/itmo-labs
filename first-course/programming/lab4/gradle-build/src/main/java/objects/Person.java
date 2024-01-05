package objects;

import services.Gender;

public class Person extends Entity {
    public Person(String name, Gender gender) {
        super(name, gender);
    }

    public String toString() {
        return super.toString() + 
            "}";
    }
}
