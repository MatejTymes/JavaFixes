package javafixes.object;

public class InitializationFailedException extends RuntimeException {

    public InitializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
