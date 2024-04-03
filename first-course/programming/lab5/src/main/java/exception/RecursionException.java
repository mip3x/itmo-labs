package exception;

/**
 * Throws in case of too deep recursion
 */
public class RecursionException extends Exception {
    public RecursionException(String message) {super(message);}
}
