package sentenceCore;

import org.apache.commons.lang3.tuple.MutablePair;

import interfaces.SentenceMember;
import interfaces.Dependent;
import interfaces.Generator;
import services.Order;
import services.MemberType;
import services.Tense;
import services.binders.Preposition;

public class Predicate extends CoreSpeech {
    private Tense tense;
    private String infinitive;
    private MutablePair<Preposition, Dependent> dependence;

    public Predicate(String mainWord, String characteristic, Tense tense, Order order) {
        super(mainWord, characteristic, order);

        setInfinitive(mainWord);
        setTense(tense);
    }

    public Predicate(String mainWord, String characteristic, Tense tense) {
        super(mainWord, characteristic);

        setInfinitive(mainWord);
        setTense(tense);
    }

    public Predicate(String mainWord, MemberType memberType, Tense tense) {
        super(mainWord, memberType);

        if (memberType == MemberType.OBJECT) setInfinitive(mainWord);

        setTense(tense);
    }

    public Tense getTense() {
        return tense;
    }

    public void setTense(Tense tense) {
        this.tense = tense;
    } 

    public String getInfinitive() {
        return infinitive;
    }

    public void setInfinitive(String infinitive) {
        this.infinitive = infinitive;
    }

    public MutablePair<Preposition, Dependent> getDependence() {
        return dependence;
    }

    public void setDependence(Preposition preposition, Dependent dependent) {
        dependence = MutablePair.of(preposition, dependent);
    }

    public void setDependence(Dependent dependent) {
        dependence = MutablePair.of(Preposition.NOTHING, dependent);
    }

    public String getPredicateWord() {
        return (String)getMainWord();
    }

    public void setPredicateWord(String verb) {
        setMainWord(verb);
    }
}
