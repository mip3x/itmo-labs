package story;

import interfaces.Bindable;

public class Sentence {
    private String content;

    public Sentence(String content) {
        this.content = content;
    }

    public void print() {
        System.out.println(content);
    }

    public void addBindable(Bindable bindable) {
        content = content + bindable.getBindableCharacters() + " ";
    }
}
