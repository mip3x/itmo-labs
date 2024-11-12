package interfaces;

import services.Case;

public interface Dependent {
    public Case getCase();
    public void setCase(Case dependentCase);
    
    public abstract String getName();
    public String getCasedName();
}
