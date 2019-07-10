package javafixes.object;

import java.util.concurrent.Callable;

// todo: add javadoc
public class Lazy<T> {

    private T value = null;
    private boolean isInitialized = false;

    private final Callable<T> valueProvider;

    public Lazy(Callable<T> valueProvider) {
        if (valueProvider == null) {
            throw new IllegalArgumentException("valueProvider of a Lazy can't be null");
        }
        this.valueProvider = valueProvider;
    }

    /**
     * Factory method for Lazy value
     *
     * @param valueProvider initialization method
     * @return {@code Lazy} value
     */
    public static <T> Lazy<T> lazy(Callable<T> valueProvider) {
        return new Lazy<>(valueProvider);
    }

    /**
     * Provides info if a successful initialization has already occurred
     * and the value has been initialized/cached.
     *
     * @return if value has been already initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * If initialization was not call successful, calls initialization and its result.
     * If initialization was successful, then on second call only the value is return
     * (and the initialization is NOT executed anymore).
     * In case the initialization has failed an {@code InitializationFailedException} is thrown.
     * Subsequent call of this method after failure will execution initialization again.
     * This can be repeated until the initialization is finally successful.
     *
     * @return initialized value (in case of initialization success)
     * @throws InitializationFailedException wrapping {@code Exception} that occurred during initialization failure
     */
    public T value() throws InitializationFailedException {
        if (!isInitialized) {
            synchronized (this) {
                if (!isInitialized) {
                    try {
                        value = valueProvider.call();
                        isInitialized = true;
                    } catch (Exception e) {
                        throw new InitializationFailedException("Failed to initialize Lazy value", e);
                    }
                }
            }
        }

        return value;
    }
}
