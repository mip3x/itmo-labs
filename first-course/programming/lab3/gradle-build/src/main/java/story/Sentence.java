package story;

import org.apache.commons.lang3.StringUtils;

import interfaces.SentenceMember;
import services.binders.Union;

public class Sentence {
    private String content;

    public Sentence(String content) {
        this.content = content;
    }

    public Sentence() {
        this.content = StringUtils.EMPTY;
    }

    public void print() {
        for (int i = 0; i < content.length() - 1; i++) {
            if (content.charAt(i + 1) != ',' && content.charAt(i + 1) != '.') {
                System.out.print(content.charAt(i));
            }
        }
        System.out.print('\n');
    }

    public void addSentenceMember(SentenceMember sentenceMember) {
        content += sentenceMember.getSentenceMemberCharacters();
    }
}
