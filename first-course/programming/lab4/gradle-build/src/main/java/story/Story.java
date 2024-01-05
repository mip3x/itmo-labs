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
        Person poohPigletTigra = new Person("они", Gender.GROUP);
        Representer representerPoohPigletTigra = new Representer(poohPigletTigra, "чем дальше");

        Person fog = new Person("туман", Gender.MALE);
        Representer representerFog = new Representer(fog, MemberType.OBJECT);

        Person tigra = new Person("Тигра", Gender.MALE);
        Representer representerTigra = new Representer(tigra, MemberType.OBJECT);

        Person you = new Person("вы", Gender.GROUP);
        Representer representerYou = new Representer(you, "когда");
        Representer representerBeforeYou = new Representer(you, "прежде чем");

        Person rabbit = new Person("Кролик", Gender.MALE);
        Representer representerRabbit = new Representer(rabbit, MemberType.OBJECT);

        Person piglet = new Person("Пятачок", Gender.MALE);
        Representer representerPiglet = new Representer(piglet, MemberType.OBJECT);

        Person poohAndPiglet = new Person("Пух и Пятачок", Gender.GROUP);
        Representer representerPoohAndPiglet = new Representer(poohAndPiglet, MemberType.OBJECT);

        Person nothing = new Person("", Gender.MIDDLE);
        Representer representerNothing = new Representer(nothing, MemberType.OBJECT);

        // places
        Place moment = new Place("момент", Gender.MALE, Preposition.IN);
        Placer momentPlacer = new Placer(moment, "тот самый");

        Place clearing = new Place("прогалина", Gender.FEMALE, Preposition.IN);
        Placer clearingPlacer = new Placer(clearing, "пересекавшую тропинку", Order.REVERSE);

        Place bushes  = new Place("папоротник", Gender.MALE, Preposition.IN);
        Placer bushesPlacer = new Placer(bushes, "высоком");

        Place forest = new Place("Лес", Gender.MALE, Preposition.IN);
        Placer forestPlacer = new Placer(forest, MemberType.OBJECT);

        // predicates
        Predicate go = new Predicate("идти", MemberType.OBJECT, Tense.PAST);
        Predicate become = new Predicate("становиться", "тем гуще", Tense.PAST);
        Predicate startMissGoing = new Predicate("стать", MemberType.OBJECT, Tense.PAST);
        Predicate missGoing = new Predicate("пропадать", "по временам", Tense.PAST);
        Predicate nothingHappening = new Predicate("", MemberType.OBJECT, Tense.PRESENT);
        Predicate think = new Predicate("думать", MemberType.OBJECT, Tense.PAST);
        Predicate notExist = new Predicate("нет", "уже совсем", Tense.PRESENT);
        Predicate appearManyTimes = new Predicate("появляться", "выпаливая: «Чего же вы? Ходу!» –", Tense.PAST, Order.REVERSE);
        Predicate keepUp = new Predicate("успевать", "что-нибудь", Tense.PAST);
        Predicate answer = new Predicate("ответить", MemberType.OBJECT, Tense.PRESENT);
        Predicate disappearManyTimes = new Predicate("исчезать", "снова", Tense.PAST);

        Predicate turnAround = new Predicate("обернуться", MemberType.OBJECT, Tense.PAST);
        Predicate push = new Predicate("подтолкнуть", "Пятачка", Tense.PAST, Order.REVERSE);

        Predicate appearOneTime = new Predicate("появиться", "неожиданно", Tense.PAST);
        Predicate runInto = new Predicate("налететь", MemberType.OBJECT, Tense.PAST);
        Predicate disappearOneTime = new Predicate("исчезнуть", "снова", Tense.PAST);

        Predicate rollUp = new Predicate("свернуть", MemberType.OBJECT, Tense.PAST);
        Predicate rush = new Predicate("помчаться", MemberType.OBJECT, Tense.PAST);

        Predicate hide = new Predicate("притаиться", MemberType.OBJECT, Tense.PAST);
        Predicate listening = new Predicate("прислушиваться", MemberType.OBJECT, Tense.PAST);

        Predicate be = new Predicate("быть", "очень, очень тихо", Tense.PAST, Order.REVERSE);
        
        Predicate see = new Predicate("не видеть", "ничего", Tense.PAST);
        Predicate hear = new Predicate("не слышать", "ничего", Tense.PAST);

        // actions 
        Action poohPigletTigraGo = new Action(representerPoohPigletTigra, go, Order.DIRECT, Order.DIRECT);
        Action fogBecome = new Action(representerFog, become, Order.REVERSE, Order.DIRECT);
        Action tigraStartMissGoing = new Action(representerTigra, startMissGoing, Order.DIRECT, Order.DIRECT);
        Action tigraMissGoing = new Action(representerTigra, missGoing, Order.DIRECT, Order.DIRECT);
        Action nothingGoingOn = new Action(representerNothing, nothingHappening, momentPlacer, Case.NOMINATIVE, Order.DIRECT, Order.DIRECT);
        Action youThink = new Action(representerYou, think, Order.DIRECT, Order.DIRECT);
        Action tigraNotExist = new Action(representerTigra, notExist, Order.DIRECT, Order.DIRECT);
        Action tigraAppearsManyTimes = new Action(representerTigra, appearManyTimes, Order.DIRECT, Order.DIRECT);
        Action youKeepUp = new Action(representerBeforeYou, keepUp, Order.DIRECT, Order.DIRECT);
        Action nothingAnswering = new Action(representerNothing, answer, Order.DIRECT, Order.DIRECT);
        Action tigraDisappearsManyTimes = new Action(representerTigra, disappearManyTimes, Order.DIRECT, Order.DIRECT);

        Action rabbitTurnsAround = new Action(representerRabbit, turnAround, Order.DIRECT, Order.DIRECT);
        Action rabbitPushesPiglet = new Action(representerRabbit, push, Order.DIRECT, Order.DIRECT);

        Action tigraAppearsOneTime = new Action(representerTigra, appearOneTime, Order.DIRECT, Order.DIRECT);
        Action tigraRunsIntoRabbit = new Action(representerTigra, runInto, Order.DIRECT, Order.DIRECT);
        Action tigraDisappearsOneTime = new Action(representerTigra, disappearOneTime, Order.DIRECT, Order.DIRECT);

        Action tigraRollsUpIntoClearing = new Action(representerTigra, rollUp, clearingPlacer, Case.ACCUSATIVE, Order.DIRECT, Order.DIRECT);
        Action poohAndPigletRush = new Action(representerPoohAndPiglet, rush, Order.DIRECT, Order.DIRECT);

        Action poohAndPigletHide = new Action(representerPoohAndPiglet, hide, bushesPlacer, Case.PREPOSITIONAL, Order.DIRECT, Order.DIRECT);
        Action poohAndPigletListening = new Action(representerPoohAndPiglet, listening, Order.DIRECT, Order.DIRECT);

        Action nothingWasQuite = new Action(representerNothing, be, forestPlacer, Case.PREPOSITIONAL, Order.DIRECT, Order.REVERSE);

        Action poohAndPigletSee = new Action(representerPoohAndPiglet, see, Order.DIRECT, Order.DIRECT);
        Action poohAndPigletHear = new Action(representerPoohAndPiglet, hear, Order.DIRECT, Order.DIRECT);

        // zero sentence
        Sentence zeroSentence = new Sentence();

        zeroSentence.addSentenceMember(Union.AND);
        zeroSentence.addSentenceMember(poohPigletTigraGo);
        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(fogBecome);

        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(tigraStartMissGoing);
        representerTigra.setHiddenStatus(true);
        zeroSentence.addSentenceMember(tigraMissGoing);

        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(Union.AND);
        representerNothing.setHiddenStatus(true);
        zeroSentence.addSentenceMember(nothingGoingOn);

        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(youThink);
        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(Union.WHAT);
        representerTigra.setName("его");
        representerTigra.setHiddenStatus(false);
        zeroSentence.addSentenceMember(tigraNotExist);

        zeroSentence.addSentenceMember(Union.COMMA);
        representerTigra.setName("он");
        zeroSentence.addSentenceMember(tigraAppearsManyTimes);

        zeroSentence.addSentenceMember(Union.AND);
        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(youKeepUp);
        zeroSentence.addSentenceMember(nothingAnswering);

        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(tigraDisappearsManyTimes);

        zeroSentence.addSentenceMember(Union.DOT);
        addSentence(zeroSentence);

        // minus first sentence
        Sentence minusFirstSentence = new Sentence();

        minusFirstSentence.addSentenceMember(rabbitTurnsAround);

        representerRabbit.setHiddenStatus(true);
        minusFirstSentence.addSentenceMember(Union.AND);

        minusFirstSentence.addSentenceMember(rabbitPushesPiglet);
        minusFirstSentence.addSentenceMember(Union.DOT);

        addSentence(minusFirstSentence);

        // first sentence
        Sentence firstSentence = new Sentence();

        representerTigra.setName("Тигра");
        firstSentence.addSentenceMember(tigraAppearsOneTime);
        firstSentence.addSentenceMember(Union.COMMA);

        representerTigra.setHiddenStatus(true);

        tigraRunsIntoRabbit.setPredicateDependence(Preposition.ON, representerRabbit, Case.ACCUSATIVE);
        
        firstSentence.addSentenceMember(tigraRunsIntoRabbit);
        firstSentence.addSentenceMember(Union.AND);
        firstSentence.addSentenceMember(tigraDisappearsOneTime);

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
