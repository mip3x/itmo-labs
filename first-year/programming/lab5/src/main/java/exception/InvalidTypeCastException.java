package exception;

/**
 * Throws in case of invalid input given to cast
 */
public class InvalidTypeCastException extends RuntimeException {
    public InvalidTypeCastException(String message) {
        super(message);
    }
}
