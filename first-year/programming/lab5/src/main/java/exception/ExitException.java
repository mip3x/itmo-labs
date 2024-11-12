package exception;

/**
 * Throws in case of exit from program
 */
public class ExitException extends Exception {
    public ExitException(String message) {super(message);}
}
