package javafixes.common.exception;

// todo: mtymes - add javadoc
public class InitializationFailedException extends RuntimeException {

    public InitializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
