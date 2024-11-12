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

        Person nothing = new Person("", Gender.MIDDLE);
        Representer representerNothing = new Representer(nothing, MemberType.OBJECT);

        // places
        Place clearing = new Place("прогалина", Gender.FEMALE, Preposition.IN);
        Placer clearingPlacer = new Placer(clearing, "пересекавшую тропинку", Order.REVERSE);

        Place bushes  = new Place("папоротник", Gender.MALE, Preposition.IN);
        Placer bushesPlacer = new Placer(bushes, "высоком");

        Place forest = new Place("Лес", Gender.MALE, Preposition.IN);
        Placer forestPlacer = new Placer(forest, MemberType.OBJECT);

        // predicates
        Predicate appear = new Predicate("появиться", "неожиданно", Tense.PAST);
        Predicate runInto = new Predicate("налететь", MemberType.OBJECT, Tense.PAST);
        Predicate disappear = new Predicate("исчезнуть", "снова", Tense.PAST);

        Predicate rollUp = new Predicate("свернуть", MemberType.OBJECT, Tense.PAST);
        Predicate rush = new Predicate("помчаться", MemberType.OBJECT, Tense.PAST);

        Predicate hide = new Predicate("притаиться", MemberType.OBJECT, Tense.PAST);
        Predicate listening = new Predicate("прислушиваться", MemberType.OBJECT, Tense.PAST);

        Predicate be = new Predicate("быть", "очень, очень тихо", Order.REVERSE, Tense.PAST);
        
        Predicate see = new Predicate("не видеть", "ничего", Tense.PAST);
        Predicate hear = new Predicate("не слышать", "ничего", Tense.PAST);

        // actions 
        Action tigraAppears = new Action(representerTigra, appear);
        Action tigraRunsIntoRabbit = new Action(representerTigra, runInto);
        Action tigraDisappears = new Action(representerTigra, disappear);

        Action tigraRollsUpIntoClearing = new Action(representerTigra, rollUp, clearingPlacer, Case.ACCUSATIVE);
        Action poohAndPigletRush = new Action(representerPoohAndPiglet, rush);

        Action poohAndPigletHide = new Action(representerPoohAndPiglet, hide, bushesPlacer, Case.PREPOSITIONAL);
        Action poohAndPigletListening = new Action(representerPoohAndPiglet, listening);

        Action nothingWasQuite = new Action(representerNothing, be, forestPlacer, Case.PREPOSITIONAL);

        Action poohAndPigletSee = new Action(representerPoohAndPiglet, see);
        Action poohAndPigletHear = new Action(representerPoohAndPiglet, hear);

        // first sentence
        Sentence firstSentence = new Sentence();

        firstSentence.addSentenceMember(tigraAppears);
        firstSentence.addSentenceMember(Union.COMMA);

        representerTigra.setHiddenStatus(true);

        tigraRunsIntoRabbit.setPredicateDependence(Preposition.ON, representerRabbit, Case.ACCUSATIVE);
        
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

        poohAndPigletRush.setPredicateDependence(Preposition.AFTER, representerTigra, Case.CREATIVE);

        secondSentence.addSentenceMember(poohAndPigletRush);

        secondSentence.addSentenceMember(Union.DOT);

        addSentence(secondSentence);


        // third sentence
        Sentence thirdSentence = new Sentence();

        representerPoohAndPiglet.setName("Они");

        thirdSentence.addSentenceMember(poohAndPigletHide);
        thirdSentence.addSentenceMember(Union.COMMA);

        representerPoohAndPiglet.setHiddenStatus(true);

        thirdSentence.addSentenceMember(poohAndPigletListening);

        thirdSentence.addSentenceMember(Union.DOT);

        addSentence(thirdSentence);

        // fourth sentence
        Sentence fourthSentence = new Sentence();

        representerNothing.setHiddenStatus(true);
        fourthSentence.addSentenceMember(nothingWasQuite);

        representerPoohAndPiglet.setHiddenStatus(true);

        fourthSentence.addSentenceMember(Union.DOT);

        addSentence(fourthSentence);

        // fifth sentence
        Sentence fifthSentence = new Sentence();

        representerPoohAndPiglet.setHiddenStatus(false);
        fifthSentence.addSentenceMember(poohAndPigletSee);
        fifthSentence.addSentenceMember(Union.AND);

        representerPoohAndPiglet.setHiddenStatus(true);

        fifthSentence.addSentenceMember(poohAndPigletHear);

        fifthSentence.addSentenceMember(Union.DOT);

        addSentence(fifthSentence);
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
