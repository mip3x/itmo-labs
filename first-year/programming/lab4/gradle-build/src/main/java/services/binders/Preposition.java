package services.binders;

import interfaces.SentenceMember;

public enum Preposition implements SentenceMember {
    ON("на "),
    IN("в "),
    AFTER("за "),
    NOTHING("");

    private String preposition;

    private Preposition(String preposition) {
        this.preposition = preposition;
    }

    @Override
    public String getSentenceMemberCharacters() {
        return preposition;
    }
}
