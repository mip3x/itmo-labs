package objects;

import services.Gender;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "Entity{" +
            "name=" + getName() + ";" +
            "gender=" + getGender();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Entity comparedEntity = (Entity) object;
        if (name == comparedEntity.name && gender == comparedEntity.gender) {
            return true;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender);
    }
}
