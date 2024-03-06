package exception;

public class InvalidCommandException extends Exception {
    private final String exceptionMessage = "Команда была введена неправильно!";

    public String getMessage() {
        return exceptionMessage;
    }
}
