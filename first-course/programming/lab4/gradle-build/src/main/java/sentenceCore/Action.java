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
import services.binders.*;

public class Action implements SentenceMember {
    private Representer representer;
    private Predicate predicate;
    private Case dependenceCase;
    private Order coreOrder;
    private Order predicateDependenceOrder;

    public Action(Representer representer, Predicate predicate, Order coreOrder, Order predicateDependenceOrder) {
        setCoreOrder(coreOrder);
        setPredicateDependenceOrder(predicateDependenceOrder);
        setRepresenter(representer);
        setPredicate(predicate);
    }

    public Action(Representer representer, Predicate predicate, Placer placer, Case dependenceCase, Order coreOrder, Order predicateDependenceOrder) {
        setCoreOrder(coreOrder);
        setPredicateDependenceOrder(predicateDependenceOrder);
        setRepresenter(representer);
        setPredicate(predicate);
        setPredicateDependence(placer.getPlace().getPreposition(), placer, dependenceCase);
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

            dependent.setCase(dependenceCase);

            dependence = preposition.getSentenceMemberCharacters();

            Subject subjectDependent = (Subject)dependent;
            if (subjectDependent.getCharacteristic() != null) {
                if (subjectDependent.getOrder() != null) {
                    switch ((Order)subjectDependent.getOrder()) {
                        case DIRECT:
                            dependence += ((String)subjectDependent.getCharacteristic()).substring(2) + " " + dependent.getCasedName();
                            System.out.println(dependence + ".");
                            break;
                        case REVERSE:
                            dependence += dependent.getCasedName() + " " + (String)subjectDependent.getCharacteristic();
                            break;
                    }
                } 
                else dependence += (String)subjectDependent.getCharacteristic() + " " + dependent.getCasedName();
            } 
            else dependence = preposition.getSentenceMemberCharacters() + dependent.getCasedName();
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
                                case "появиться" -> this.predicate.setPredicateWord("появился");
                                case "налететь" -> this.predicate.setPredicateWord("налетел");
                                case "исчезнуть" -> this.predicate.setPredicateWord("исчез");
                                case "свернуть" -> this.predicate.setPredicateWord("свернул");
                                case "становиться" -> this.predicate.setPredicateWord("становился");
                                case "стать" -> this.predicate.setPredicateWord("стал");
                                case "появляться" -> this.predicate.setPredicateWord("появлялся");
                                case "исчезать" -> this.predicate.setPredicateWord("исчезал");
                                case "обернуться" -> this.predicate.setPredicateWord("обернулся");
                                case "подтолкнуть" -> this.predicate.setPredicateWord("подтолкнул"); 
                                case "раздаться" -> this.predicate.setPredicateWord("раздался");
                            }
                    }
                case MIDDLE:
                    switch (predicateTense) {
                        case PAST:
                            switch (predicateInfinitive) {
                                case "быть" -> this.predicate.setPredicateWord("было");
                                case "наступить" -> this.predicate.setPredicateWord("наступило"); 
                            }
                    }
                case GROUP:
                    switch (predicateTense) {
                        case PAST:
                            switch (predicateInfinitive) {
                                case "помчаться" -> this.predicate.setPredicateWord("помчались");
                                case "притаиться" -> this.predicate.setPredicateWord("притаились");
                                case "прислушиваться" -> this.predicate.setPredicateWord("прислушиваясь");
                                case "не видеть" -> this.predicate.setPredicateWord("не видели");
                                case "не слышать" -> this.predicate.setPredicateWord("не слышали");
                                case "идти" -> this.predicate.setPredicateWord("шли");
                                case "думать" -> this.predicate.setPredicateWord("думали");
                                case "успевать" -> this.predicate.setPredicateWord("успевали"); 
                            }
                    }
            }
            String currentPredicateWord = this.predicate.getPredicateWord();

            if (dependence != "") {
                if (currentPredicateWord != "") {
                    switch (predicateDependenceOrder) {
                        case DIRECT -> this.predicate.setPredicateWord(currentPredicateWord + " " + dependence);
                        case REVERSE -> this.predicate.setPredicateWord(dependence + " " + currentPredicateWord);
                    }
                }
                else this.predicate.setPredicateWord(dependence);
            }
        }
    }

    public Order getCoreOrder() {
        return coreOrder;
    }

    public void setCoreOrder(Order coreOrder) {
        this.coreOrder = coreOrder;
    }
    
    public Order getPredicateDependenceOrder() {
        return predicateDependenceOrder;
    }

    public void setPredicateDependenceOrder(Order predicateDependenceOrder) {
        this.predicateDependenceOrder = predicateDependenceOrder;
    }

    public void setPredicateDependence(Preposition preposition, Dependent dependent, Case dependenceCase) {
        this.dependenceCase = dependenceCase;
        predicate.setDependence(preposition, dependent);
        setPredicate(predicate);
    }

    @Override
    public String getSentenceMemberCharacters() {
        String result = "";
        if (representer.equals(null) || representer.getHiddenStatus()) result = predicate.getSentenceMemberCharacters();
        else {
            switch (coreOrder) {
                case DIRECT -> result = representer.getSentenceMemberCharacters() + predicate.getSentenceMemberCharacters();
                case REVERSE -> result = predicate.getSentenceMemberCharacters() + representer.getSentenceMemberCharacters();
            } 
        } 

        return result;
    }
}
