package story;

import org.apache.commons.lang3.StringUtils;

import interfaces.SentenceMember;
import services.binders.Union;

public class Sentence {
    private String content;
    private final String alphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public Sentence(String content) {
        this.content = content;
    }

    public Sentence() {
        this.content = StringUtils.EMPTY;
    }

    public void print() {
        for (int i = 0; i < content.length() - 1; i++) {
            if (content.charAt(i + 1) != '.') {
                if (content.charAt(i + 1) == ',') {
                    for (int j = 0; j < alphabet.length() - 1; j++) {
                        if (content.charAt(i) == alphabet.charAt(j)) {
                            System.out.print(content.charAt(i));
                            break;
                        }
                    }
                } 
                else {
                    System.out.print(content.charAt(i));
                }
            }
        }
        System.out.print('\n');
    }

    public void addSentenceMember(SentenceMember sentenceMember) {
        content += sentenceMember.getSentenceMemberCharacters();
    }
}
