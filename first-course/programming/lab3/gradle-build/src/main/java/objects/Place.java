package objects;

import services.Gender;
import services.binders.Preposition;

public class Place extends Entity {
    private Preposition preposition;

    public Place(String name, Gender gender, Preposition preposition) {
        super(name, gender);
        this.preposition = preposition;
    }

    public Preposition getPreposition() {
        return preposition;
    }

    public void setPreposition(Preposition preposition) {
        this.preposition = preposition;
    }
}
