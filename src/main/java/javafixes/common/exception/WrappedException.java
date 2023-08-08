package javafixes.common.exception;

public class WrappedException extends RuntimeException {

    public WrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrappedException(Throwable cause) {
        super(cause);
    }
}
