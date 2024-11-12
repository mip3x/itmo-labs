package services.binders;

import interfaces.SentenceMember;

public enum Union implements SentenceMember {
    COMMA(", "),
    AND("и "),
    DOT(". "),
    WHAT("что "),
    TO("чтобы "),
    BUTA("а ");
        
    private String union;

    private Union(String union) {
        this.union = union;
    }

    @Override
    public String getSentenceMemberCharacters() {
        return union;
    }
}
