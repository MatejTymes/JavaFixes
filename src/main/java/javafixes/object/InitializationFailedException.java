package javafixes.object;

// todo: move into common.exception
public class InitializationFailedException extends RuntimeException {

    public InitializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
