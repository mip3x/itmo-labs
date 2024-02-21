package console;

import java.util.Scanner;

public class ConsoleHandler {
    public Scanner scanner;
    private final String prompt;

    public ConsoleHandler() {
        this.scanner = new Scanner(System.in);
        this.prompt = "[~]: ";
    }

    public String receive() {
        printPrompt();
        String line = scanner.nextLine();
        return line;
    }

    public void send(String message) {
        System.out.println(message);
    }

    public void printPrompt() {
        System.out.print(prompt);
    }
}
