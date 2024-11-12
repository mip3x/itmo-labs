package exception;

/**
 * Throws in case of invalid input
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
