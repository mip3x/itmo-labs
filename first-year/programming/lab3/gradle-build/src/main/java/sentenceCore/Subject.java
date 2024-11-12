package sentenceCore;

import interfaces.Dependent;
import services.Order;
import services.MemberType;
import services.Case;
import objects.Entity;

public abstract class Subject extends CoreSpeech implements Dependent {
    private Case dependentCase;
    protected Entity entity;
    private String VOWELS = "aeiouAEIOU";

    public Subject(Entity entity, String characteristic, Order order) {
        super(entity.getName(), characteristic, order);
    }

    public Subject(Entity entity, String characteristic) {
        super(entity.getName(), characteristic);
    }

    public Subject(Entity entity, MemberType memberType) {
        super(entity.getName(), memberType);
    }

    public Subject(String word, MemberType memberType) {
        super(word, memberType);
    }

    public void setName(String name) {
        setMainWord(name);
    }

    protected Entity getEntity() {
       return entity; 
    };

    protected void setEntity(Entity entity) {
       this.entity = entity; 
    };

    @Override
    public abstract String getName(); 

    @Override
    public String getCasedName() {
        return (String)getMainWord();
    }

    @Override
    public Case getCase() {
        return dependentCase;
    }

    @Override
    public void setCase(Case dependentCase) {
        this.dependentCase = dependentCase;

        switch (entity.getGender()) {
            case MALE:
                switch (this.dependentCase) {
                    case ACCUSATIVE:
                        if (VOWELS.indexOf(entity.getName().charAt(entity.getName().length() - 1)) == -1) setMainWord(entity.getName() + "а");
                        else setMainWord(entity.getName() + "у");
                        break;
                    case CREATIVE:
                        if (((String)getMainWord()).toLowerCase().equals("он")) setMainWord("ним");
                        break;
                    case PREPOSITIONAL:
                        switch (entity.getName()) {
                            case "Лес" -> setMainWord(entity.getName() + "у");
                            case "папоротник" -> setMainWord(entity.getName() + "е");
                        }
                        break;
                }
                break;
            case FEMALE:
                switch (this.dependentCase) {
                    case ACCUSATIVE -> setMainWord(entity.getName().substring(0, entity.getName().length() - 1) + "у");
                }
                break;
        }
    }
}
