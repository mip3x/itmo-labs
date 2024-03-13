package console;

import java.util.Scanner;

public class ConsoleHandler {
    public Scanner scanner;
    private final String prompt;

    public ConsoleHandler() {
        this.scanner = new Scanner(System.in);
        this.prompt = "[~]";
    }

    public String receive(boolean printPrompt) {
        if (printPrompt) printPrompt();
        return scanner.nextLine();
    }

    public String receive() {
        return receive(false);
    }

    public String receive(String customPrompt) {
        printPrompt(customPrompt);
        return receive();
    }

    public void send(String message) {
        System.out.print(message);
    }

    public void sendWithNewLine(String message) {
        System.out.println(message);
    }

    public void printPrompt() {
        System.out.print(prompt + ": ");
    }

    public void printPrompt(String customPrompt) {
        System.out.print(customPrompt + ": ");
    }
}
