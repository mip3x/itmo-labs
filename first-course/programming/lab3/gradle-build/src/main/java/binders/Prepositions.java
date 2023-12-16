package binders;

import interfaces.Bindable;

public enum Prepositions implements Bindable {
    ON("на"),
    IN("в"),
    AFTER("за");

    private String preposition;

    private Prepositions(String preposition) {
        this.preposition = preposition;
    }

    @Override
    public String getBindableCharacters() {
        return preposition;
    }
}
