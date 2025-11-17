package app.exception;

public class PasswordMatchesException extends RuntimeException {

    public PasswordMatchesException() {
    }

    public PasswordMatchesException(String message) {
        super(message);
    }
}
