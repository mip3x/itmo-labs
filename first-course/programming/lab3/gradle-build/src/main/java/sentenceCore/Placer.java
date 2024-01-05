package sentenceCore;

import objects.Place;
import objects.Entity;
import services.Order;
import services.MemberType;
import services.Case;

public class Placer extends Subject {
    private Place place;

    public Placer(Place place, String characteristic, Order order) {
        super(place, characteristic, order);

        setPlace(place);
    }

    public Placer(Place place, String characteristic) {
        super(place, characteristic);

        setPlace(place);
    }

    public Placer(Place place, MemberType memberType) {
        super(place, memberType);

        setPlace(place);
    }

    public Placer(String word, MemberType memberType) {
        super(word, memberType);
    }

    public Place getPlace() {
        return place;
    }
    
    public void setPlace(Place place) {
        this.place = place;
        setEntity((Entity)place);
    }

    public String getName() {
        return entity.getName();
    }
}
