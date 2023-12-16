package story;

import java.util.ArrayList;
import java.util.List;
import binders.*;

public class Story {
    private List<Sentence> sentences;

    public Story() {
        this.sentences = new ArrayList<>();

        Sentence sentence = new Sentence("Hello, world");
        sentence.addBindable(Unions.COMMA);
        sentence.addBindable(Prepositions.AFTER);
        sentences.add(sentence);
    }

    public void startTheTell() {
        for (var sentence : sentences) {
            sentence.print();
        }
    }

    public void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }
}
