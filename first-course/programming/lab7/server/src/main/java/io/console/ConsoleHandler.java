package io.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ConsoleHandler {
    private final BufferedReader reader;
    private final String prompt;

    public ConsoleHandler() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.prompt = "[server]";
    }

    public void write(Consumer<String> method, String message) {
        method.accept(message);
    }

    public void writeWithNewLine(Consumer<String> method, String message) {
        System.out.println();
        write(method, message);
    }

    public void writeWithPrompt(Consumer<String> method, String message) {
        write(method, message);
        printPrompt();
    }

    public void writeWithPromptNewLine(Consumer<String> method, String message) {
        writeWithNewLine(method, message);
        printPrompt();
    }

    public String receive() throws IOException {
        return reader.readLine();
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    public void printPrompt() {
        System.out.print(prompt + ": ");
    }
}
