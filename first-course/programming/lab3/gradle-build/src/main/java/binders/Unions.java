package binders;

import interfaces.Bindable;

public enum Unions implements Bindable {
    COMMA(","),
    AND("и");

    private String union;

    private Unions(String union) {
        this.union = union;
    }

    @Override
    public String getBindableCharacters() {
        return union;
    }
}
