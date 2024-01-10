package story;

import java.util.ArrayList;
import java.util.List;

import interfaces.Generator;
import services.*;
import services.binders.*;
import sentenceCore.*;
import objects.*;

public class Story {
    private List<Sentence> sentences;

    public Story() {
        this.sentences = new ArrayList<>();

        // persons
        Person illegalPerson = new Person("l1123", Gender.MALE);

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

        Person footSteps = new Person("топот", Gender.MALE);
        Representer representerFootSteps = new Representer(footSteps, MemberType.OBJECT);

        Person silence = new Person("молчание", Gender.MIDDLE);
        Representer representerSilence = new Representer(silence, MemberType.OBJECT);

        Person pooh = new Person("Пух", Gender.MALE);
        Representer representerPooh = new Representer(pooh, MemberType.OBJECT);

        Person smellOfRawFern = new Person("запах", Gender.MALE);
        Representer representerSmellOfRawFern = new Representer(smellOfRawFern, "сырого папоротника", Order.REVERSE);

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
        Predicate push = new Predicate("подтолкнуть", MemberType.OBJECT, Tense.PAST);

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

        Predicate ringOut = new Predicate("раздаться", MemberType.OBJECT, Tense.PAST);

        Predicate come = new Predicate("наступить", "снова", Tense.PAST);

        Predicate nudge = new Predicate("подтолкнуть", "локтем", Tense.PAST, Order.REVERSE);
        Predicate lookAround = new Predicate("оглянуться", "в поисках", Tense.PAST, Order.REVERSE);
        Predicate find = new Predicate("не найти", MemberType.OBJECT, Tense.PAST); 
        Predicate keepInhaling = new Predicate("продолжать вдыхать", MemberType.OBJECT, Tense.PAST);
        Predicate tryingBreathe = new Predicate("стараться дышать", "как можно тише", Tense.PRESENT, Order.REVERSE);
        Predicate feelYourself = new Predicate("чувствовать", "себя очень храбро", Tense.PAST, Order.REVERSE);

        // zero sentence
        Sentence zeroSentence = new Sentence();

        zeroSentence.addSentenceMember(Union.AND);

        Action poohPigletTigraGo = new Action(representerPoohPigletTigra, go, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(poohPigletTigraGo);

        zeroSentence.addSentenceMember(Union.COMMA);

        Action fogBecome = new Action(representerFog, become, Order.REVERSE, Order.DIRECT);
        zeroSentence.addSentenceMember(fogBecome);

        zeroSentence.addSentenceMember(Union.COMMA);

        Action tigraStartMissGoing = new Action(representerTigra, startMissGoing, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(tigraStartMissGoing);

        representerTigra.setHiddenStatus(true);

        Action tigraMissGoing = new Action(representerTigra, missGoing, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(tigraMissGoing);

        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(Union.AND);
        representerNothing.setHiddenStatus(true);

        Action nothingGoingOn = new Action(representerNothing, nothingHappening, momentPlacer, Case.NOMINATIVE, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(nothingGoingOn);

        zeroSentence.addSentenceMember(Union.COMMA);

        Action youThink = new Action(representerYou, think, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(youThink);

        zeroSentence.addSentenceMember(Union.COMMA);
        zeroSentence.addSentenceMember(Union.WHAT);
        representerTigra.setName("его");
        representerTigra.setHiddenStatus(false);

        Action tigraNotExist = new Action(representerTigra, notExist, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(tigraNotExist);

        zeroSentence.addSentenceMember(Union.COMMA);
        representerTigra.setName("он");

        Action tigraAppearsManyTimes = new Action(representerTigra, appearManyTimes, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(tigraAppearsManyTimes);

        zeroSentence.addSentenceMember(Union.AND);
        zeroSentence.addSentenceMember(Union.COMMA);

        Action youKeepUp = new Action(representerBeforeYou, keepUp, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(youKeepUp);

        Action nothingAnswering = new Action(representerNothing, answer, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(nothingAnswering);

        zeroSentence.addSentenceMember(Union.COMMA);

        Action tigraDisappearsManyTimes = new Action(representerTigra, disappearManyTimes, Order.DIRECT, Order.DIRECT);
        zeroSentence.addSentenceMember(tigraDisappearsManyTimes);

        zeroSentence.addSentenceMember(Union.DOT);
        addSentence(zeroSentence);

        // minus first sentence

        Sentence minusFirstSentence = new Sentence();

        Action rabbitTurnsAround = new Action(representerRabbit, turnAround, Order.DIRECT, Order.DIRECT);
        minusFirstSentence.addSentenceMember(rabbitTurnsAround);

        representerRabbit.setHiddenStatus(true);
        minusFirstSentence.addSentenceMember(Union.AND);

        Action rabbitPushesPiglet = new Action(representerRabbit, push, representerPiglet, Case.ACCUSATIVE, Order.DIRECT, Order.DIRECT);
        minusFirstSentence.addSentenceMember(rabbitPushesPiglet);
        representerPiglet.setName("Пятачок");

        minusFirstSentence.addSentenceMember(Union.DOT);

        addSentence(minusFirstSentence);

        // first sentence
        Sentence firstSentence = new Sentence();

        representerTigra.setName("Тигра");

        Action tigraAppearsOneTime = new Action(representerTigra, appearOneTime, Order.DIRECT, Order.DIRECT);
        firstSentence.addSentenceMember(tigraAppearsOneTime);

        firstSentence.addSentenceMember(Union.COMMA);

        representerTigra.setHiddenStatus(true);

        Action tigraRunsIntoRabbit = new Action(representerTigra, runInto, Preposition.ON, representerRabbit, Case.ACCUSATIVE, Order.DIRECT, Order.DIRECT);
        firstSentence.addSentenceMember(tigraRunsIntoRabbit);
        representerRabbit.setName("Кролик");

        firstSentence.addSentenceMember(Union.AND);

        Action tigraDisappearsOneTime = new Action(representerTigra, disappearOneTime, Order.DIRECT, Order.DIRECT);
        firstSentence.addSentenceMember(tigraDisappearsOneTime);

        firstSentence.addSentenceMember(Union.DOT);

        addSentence(firstSentence);

        // second sentence
        Sentence secondSentence = new Sentence();

        representerTigra.setName("Он");
        representerTigra.setHiddenStatus(false);

        Action tigraRollsUpIntoClearing = new Action(representerTigra, rollUp, clearingPlacer, Case.ACCUSATIVE, Order.DIRECT, Order.DIRECT);
        secondSentence.addSentenceMember(tigraRollsUpIntoClearing);

        secondSentence.addSentenceMember(Union.COMMA);
        secondSentence.addSentenceMember(Union.AND);

        Action poohAndPigletRush = new Action(representerPoohAndPiglet, rush, Preposition.AFTER, representerTigra, Case.CREATIVE, Order.DIRECT, Order.DIRECT);
        secondSentence.addSentenceMember(poohAndPigletRush);

        secondSentence.addSentenceMember(Union.DOT);

        addSentence(secondSentence);

        // third sentence
        Sentence thirdSentence = new Sentence();

        representerPoohAndPiglet.setName("Они");

        Action poohAndPigletHide = new Action(representerPoohAndPiglet, hide, bushesPlacer, Case.PREPOSITIONAL, Order.DIRECT, Order.DIRECT);
        thirdSentence.addSentenceMember(poohAndPigletHide);

        thirdSentence.addSentenceMember(Union.COMMA);

        representerPoohAndPiglet.setHiddenStatus(true);

        Action poohAndPigletListening = new Action(representerPoohAndPiglet, listening, Order.DIRECT, Order.DIRECT);
        thirdSentence.addSentenceMember(poohAndPigletListening);

        thirdSentence.addSentenceMember(Union.DOT);

        addSentence(thirdSentence);

        // fourth sentence
        Sentence fourthSentence = new Sentence();

        Action nothingWasQuite = new Action(representerNothing, be, forestPlacer, Case.PREPOSITIONAL, Order.DIRECT, Order.REVERSE);
        fourthSentence.addSentenceMember(nothingWasQuite);

        representerPoohAndPiglet.setHiddenStatus(true);

        fourthSentence.addSentenceMember(Union.DOT);

        addSentence(fourthSentence);

        // fifth sentence
        Sentence fifthSentence = new Sentence();

        representerPoohAndPiglet.setHiddenStatus(false);

        Action poohAndPigletSee = new Action(representerPoohAndPiglet, see, Order.DIRECT, Order.DIRECT);
        fifthSentence.addSentenceMember(poohAndPigletSee);

        fifthSentence.addSentenceMember(Union.AND);

        representerPoohAndPiglet.setHiddenStatus(true);

        Action poohAndPigletHear = new Action(representerPoohAndPiglet, hear, Order.DIRECT, Order.DIRECT);
        fifthSentence.addSentenceMember(poohAndPigletHear);

        fifthSentence.addSentenceMember(Union.DOT);

        addSentence(fifthSentence);

        // sixth sentence
        Sentence sixthSentence = new Sentence();

        Action footStepsRingOut = new Action(representerFootSteps, ringOut, Order.REVERSE, Order.DIRECT);
        sixthSentence.addSentenceMember(footStepsRingOut);

        sixthSentence.addSentenceMember(Union.DOT);
        sixthSentence.addSentenceMember(Union.DOT);
        sixthSentence.addSentenceMember(Union.DOT);

        addSentence(sixthSentence);

        // seventh sentence
        Sentence seventhSentence = new Sentence();

        seventhSentence.addSentenceMember(Union.AND);

        Action silenceCome = new Action(representerSilence, come, Order.REVERSE, Order.DIRECT);
        seventhSentence.addSentenceMember(silenceCome);

        seventhSentence.addSentenceMember(Union.DOT);

        addSentence(seventhSentence);

        // eighth sentence
        Sentence eighthSentence = new Sentence();

        representerRabbit.setHiddenStatus(false);
        Action rabbitNudgesPooh = new Action(representerRabbit, nudge, representerPooh, Case.ACCUSATIVE, Order.DIRECT, Order.DIRECT);
        eighthSentence.addSentenceMember(rabbitNudgesPooh);
        representerPooh.setName("Пух");

        eighthSentence.addSentenceMember(Union.COMMA);
        eighthSentence.addSentenceMember(Union.AND);

        Action poohLooksAroundForPiglet = new Action(representerPooh, lookAround, representerPiglet, Case.GENITIVE, Order.DIRECT, Order.DIRECT);
        eighthSentence.addSentenceMember(poohLooksAroundForPiglet);
        representerPiglet.setName("Пятачок");

        eighthSentence.addSentenceMember(Union.COMMA);
        eighthSentence.addSentenceMember(Union.TO);

        Generator<Predicate> nudgeInfinitiveGenerator = new Generator<Predicate>() {
            @Override
            public Predicate generate() {
                return new Predicate(nudge.getInfinitive(), (String)nudge.getCharacteristic(), Tense.PRESENT, Order.REVERSE);
            }
        };

        representerPiglet.setName("Он");
        representerPooh.setHiddenStatus(true);

        Action poohNudgesPiglet = new Action(representerPooh, nudgeInfinitiveGenerator.generate(), representerPiglet, Case.ACCUSATIVE, Order.DIRECT, Order.REVERSE);
        eighthSentence.addSentenceMember(poohNudgesPiglet);
        representerPiglet.setName("Он");

        eighthSentence.addSentenceMember(Union.COMMA);
        eighthSentence.addSentenceMember(Union.AND);
        Action poohFindsPiglet = new Action(representerPooh, find, representerPiglet, Case.ACCUSATIVE, Order.DIRECT, Order.DIRECT);
        eighthSentence.addSentenceMember(poohFindsPiglet);
        representerPiglet.setName("Пятачок");

        eighthSentence.addSentenceMember(Union.COMMA);
        eighthSentence.addSentenceMember(Union.BUTA);
        Action pigletKeepsInhaling = new Action(representerPiglet, keepInhaling, representerSmellOfRawFern, Case.NOMINATIVE, Order.DIRECT, Order.DIRECT);
        eighthSentence.addSentenceMember(pigletKeepsInhaling);

        eighthSentence.addSentenceMember(Union.COMMA);
        representerPiglet.setHiddenStatus(true);
        Action pigletTryingBreathe = new Action(representerPiglet, tryingBreathe, Order.DIRECT, Order.DIRECT);
        eighthSentence.addSentenceMember(pigletTryingBreathe);

        eighthSentence.addSentenceMember(Union.COMMA);
        eighthSentence.addSentenceMember(Union.AND);
        Action pigletFeelHimselfBrave = new Action(representerPiglet, feelYourself, Order.DIRECT, Order.DIRECT);
        eighthSentence.addSentenceMember(pigletFeelHimselfBrave);

        eighthSentence.addSentenceMember(Union.DOT);

        addSentence(eighthSentence);
    }

    public void startTheTell() throws StoryIsNotReadyException {
        if (!sentences.isEmpty()) {
            for (var sentence : sentences) {
                sentence.print();
            }
        }
        else {
            throw new StoryIsNotReadyException("История не может быть рассказана: нет предложений!");
        }
    }

    private void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }
}
