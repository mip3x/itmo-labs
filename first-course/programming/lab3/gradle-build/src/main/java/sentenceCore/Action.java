package sentenceCore;

import java.util.Map;
import java.util.HashMap;

import interfaces.SentenceMember;
import interfaces.Dependent;
import services.Order;
import services.MemberType;
import services.Tense;
import services.Gender;
import services.Case;
import services.binders.Preposition;

public class Action implements SentenceMember {
    private Representer representer;
    private Predicate predicate;

    public Action(Representer representer, Predicate predicate) {
        setRepresenter(representer);
        setPredicate(predicate);
    }

    public Action(Representer representer, Predicate predicate, Placer placer) {
        setRepresenter(representer);
        setPredicate(predicate);
        setPredicateDependence(placer.getPlace().getPreposition(), placer);
    }

    public Representer getRepresenter() {
        return representer;
    }

    public void setRepresenter(Representer representer) {
        this.representer = representer;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
        String dependence = "";

        if (this.predicate.getDependence() != null) {
            Preposition preposition = this.predicate.getDependence().getLeft(); 
            Dependent dependent = this.predicate.getDependence().getRight();

            switch (preposition) {
                case ON -> dependent.setCase(Case.ACCUSATIVE);
                case IN -> dependent.setCase(Case.ACCUSATIVE);
                case AFTER -> dependent.setCase(Case.CREATIVE);
            }

            dependence = " " + preposition.getSentenceMemberCharacters() + dependent.getCasedName();
        }

        String predicateInfinitive = this.predicate.getInfinitive().toLowerCase();
        Tense predicateTense = this.predicate.getTense();

        if (representer != null) {
            switch (representer.getPerson().getGender())
            {
                case MALE:
                    switch (predicateTense) {
                        case PAST:
                            switch (predicateInfinitive) {
                                case "появиться" -> this.predicate.setPredicateWord("появился" + dependence);
                                case "налететь" -> this.predicate.setPredicateWord("налетел" + dependence);
                                case "исчезнуть" -> this.predicate.setPredicateWord("исчез" + dependence);
                                case "свернуть" -> this.predicate.setPredicateWord("свернул" + dependence);
                            }
                    }
                case MIDDLE:
                    switch (predicateTense) {
                        case PAST:
                            switch (predicateInfinitive) {
                                case "быть" -> this.predicate.setPredicateWord("было" + dependence);
                            }
                    }
                case GROUP:
                    switch (predicateTense) {
                        case PAST:
                            switch (predicateInfinitive) {
                                case "помчаться" -> this.predicate.setPredicateWord("помчались" + dependence);
                                case "притаиться" -> this.predicate.setPredicateWord("притаились" + dependence);
                                case "видеть" -> this.predicate.setPredicateWord("видели" + dependence);
                                case "слышать" -> this.predicate.setPredicateWord("слышали" + dependence);
                            }
                    }
            }
        }
        else {}
    }

    public void setPredicateDependence(Preposition preposition, Dependent dependent) {
        predicate.setDependence(preposition, dependent);
        setPredicate(predicate);
    }

    @Override
    public String getSentenceMemberCharacters() {
        String result;
        if (representer == null || representer.getHiddenStatus()) result = predicate.getSentenceMemberCharacters();
        else result = representer.getSentenceMemberCharacters() + predicate.getSentenceMemberCharacters();

        return result;
    }
}
