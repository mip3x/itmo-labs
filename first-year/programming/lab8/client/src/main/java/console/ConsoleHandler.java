package console;

import java.util.Scanner;

/**
 * Handles input and output
 */
public class ConsoleHandler {
    public Scanner scanner;
    private final String prompt;

    public ConsoleHandler() {
        this.scanner = new Scanner(System.in);
        this.prompt = "[client]";
    }

    /**
     * Prints prompt
     * @param printPrompt print prompt or not
     * @return nextLine to fill in
     */
    public String receive(boolean printPrompt) {
        if (printPrompt) printPrompt();
        return scanner.nextLine();
    }

    /**
     * Prints without prompt
     * @return nexLine to fill in
     */
    public String receive() {
        return receive(false);
    }

    /**
     * Prints custom prompt
     * @param customPrompt prompt to print
     * @return receive() output
     */
    public String receive(String customPrompt) {
        printPrompt(customPrompt);
        return receive();
    }

    /**
     * Sends message in current line
     * @param message message to print
     */
    public void send(String message) {
        System.out.print(message);
    }

    /**
     * Sends message in next line
     * @param message message to print
     */
    public void sendWithNewLine(String message) {
        System.out.println(message);
    }

    /**
     * Prints default prompt
     */
    public void printPrompt() {
        System.out.print(prompt + ": ");
    }

    /**
     * Prints custom prompt
     * @param customPrompt custom prompt to print
     */
    public void printPrompt(String customPrompt) {
        System.out.print(customPrompt + ": ");
    }
}
