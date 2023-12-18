package objects;

import services.Gender;

public abstract class Entity {
    private String name;
    private Gender gender;

    public Entity(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }
}
