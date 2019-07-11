package javafixes.object;

import java.util.concurrent.Callable;

/**
 * {@code Lazy} is intended as wrapper a lazy initialized objects.
 * It wraps an initialization method, which is called only once the {@code value()} method is invoked.
 * If initialization method provided a value, this value is cached an returned with every following {@code value()} call.
 * Also the initialization is never triggered again.
 * In case the initialization resulted in an {@link Exception} then the {@code Lazy} is being considered uninitialized
 * and the following {@code value()} call will result into invoking the initialization method again.
 * This can be repeated until the initialization method is not successful.
 *
 * @author mtymes
 */
public class Lazy<T> {

    private T value = null;
    private boolean isInitialized = false;

    private final Callable<T> valueProvider;

    /**
     * Constructor of {@code Lazy} with value initialization method
     *
     * @param valueProvider initialization method
     * @throws IllegalArgumentException if {@code null} is passed as input parameter
     */
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
