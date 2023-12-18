package sentenceCore;

import objects.Person;
import objects.Entity;
import services.Order;
import services.MemberType;
import services.Case;

public class Representer extends Subject {
    private Person person;
    private Boolean isHidden = false;

    public Representer(Person person, String characteristic, Order order) {
        super(person, characteristic, order);

        setPerson(person);
    }

    public Representer(Person person, String characteristic) {
        super(person, characteristic);

        setPerson(person);
    }

    public Representer(Person person, MemberType memberType) {
        super(person, memberType);

        setPerson(person);
    }

    public Representer(String word, MemberType memberType) {
        super(word, memberType);
    }

    public Boolean getHiddenStatus() {
        return isHidden;
    }

    public void setHiddenStatus(Boolean status) {
        isHidden = status;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        setEntity((Entity)person);
    } 

    public String getName() {
        return entity.getName();
    }

    protected Entity getEntity() {
        return entity;
    }

    protected void setEntity(Entity entity) {
        this.entity = entity;
    }
}
