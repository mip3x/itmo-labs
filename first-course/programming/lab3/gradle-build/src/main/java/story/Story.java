package story;

import java.util.ArrayList;
import java.util.List;

import services.*;
import services.binders.*;
import sentenceCore.*;
import objects.*;

public class Story {
    private List<Sentence> sentences;

    public Story() {
        this.sentences = new ArrayList<>();

        // persons
        Person tigra = new Person("Тигра", Gender.MALE);
        Representer representerTigra = new Representer(tigra, MemberType.OBJECT);

        Person rabbit = new Person("Кролик", Gender.MALE);
        Representer representerRabbit = new Representer(rabbit, MemberType.OBJECT);

        Person poohAndPiglet = new Person("Пух и Пятачок", Gender.GROUP);
        Representer representerPoohAndPiglet = new Representer(poohAndPiglet, MemberType.OBJECT);

        // places
        Place clearing = new Place("прогалина", Gender.FEMALE, Preposition.IN);
        Placer clearingPlacer = new Placer(clearing, MemberType.OBJECT);

        // predicates
        Predicate appear = new Predicate("появиться", "неожиданно", Tense.PAST);
        Predicate runInto = new Predicate("налететь", MemberType.OBJECT, Tense.PAST);
        Predicate disappear = new Predicate("исчезнуть", "снова", Tense.PAST);

        Predicate rollUp = new Predicate("свернуть", MemberType.OBJECT, Tense.PAST);
        Predicate rush = new Predicate("помчаться", MemberType.OBJECT, Tense.PAST);

        // actions 
        Action tigraAppears = new Action(representerTigra, appear);
        Action tigraRunsIntoRabbit = new Action(representerTigra, runInto);
        Action tigraDisappears = new Action(representerTigra, disappear);

        Action tigraRollsUpIntoClearing = new Action(representerTigra, rollUp, clearingPlacer);
        Action poohAndPigletRush = new Action(representerPoohAndPiglet, rush);

        // first sentence
        Sentence firstSentence = new Sentence();

        firstSentence.addSentenceMember(tigraAppears);
        firstSentence.addSentenceMember(Union.COMMA);

        representerTigra.setHiddenStatus(true);

        tigraRunsIntoRabbit.setPredicateDependence(Preposition.ON, representerRabbit);
        
        firstSentence.addSentenceMember(tigraRunsIntoRabbit);
        firstSentence.addSentenceMember(Union.AND);
        firstSentence.addSentenceMember(tigraDisappears);

        firstSentence.addSentenceMember(Union.DOT);

        addSentence(firstSentence);

        // second sentence
        Sentence secondSentence = new Sentence();

        representerTigra.setName("Он");
        representerTigra.setHiddenStatus(false);

        secondSentence.addSentenceMember(tigraRollsUpIntoClearing);
        secondSentence.addSentenceMember(Union.COMMA);
        secondSentence.addSentenceMember(Union.AND);

        poohAndPigletRush.setPredicateDependence(Preposition.AFTER, representerTigra);
        secondSentence.addSentenceMember(poohAndPigletRush);

        secondSentence.addSentenceMember(Union.DOT);

        addSentence(secondSentence);
    }

    public void startTheTell() {
        for (var sentence : sentences) {
            sentence.print();
        }
    }

    private void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }
}
