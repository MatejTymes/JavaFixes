package javafixes.object;

import javafixes.common.exception.InitializationFailedException;

import java.util.concurrent.Callable;
import java.util.function.Function;

import static javafixes.common.util.AssertUtil.assertNotNull;

/**
 * {@link Lazy} is intended as wrapper for lazily initialized objects.
 * It wraps an initialization method, which is called only once the {@code value()} method is invoked
 * (so it might be never called at all).
 * If initialization method provided a value, this value is cached an returned with every following {@code value()} call
 * and the initialization will be never triggered again.
 * In case the initialization resulted in an {@link Exception} then the {@link Lazy} is being considered uninitialized
 * and the following {@code value()} call will result into invoking the initialization method again.
 * This can be repeated until the initialization method is successful.
 *
 * @param <T> type of wrapped value
 * @author mtymes
 */
public class Lazy<T> implements Value<T> {

    private T value = null;
    private boolean isInitialized = false;

    private final Callable<T> valueProvider;

    /**
     * Constructor of {@link Lazy} with value initialization method
     *
     * @param valueProvider initialization method
     * @throws IllegalArgumentException if {@code null} is passed as input parameter
     */
    public Lazy(Callable<T> valueProvider) {
        assertNotNull(valueProvider, "valueProvider", this.getClass());

        this.valueProvider = valueProvider;
    }

    /**
     * Factory method for Lazy value
     *
     * @param valueProvider initialization method
     * @return {@link Lazy} value
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
     * If {@link Lazy} is not yet initialized call, calls initialization and caches and returns its result.
     * If initialization was successful then on second call only the cached value is returned
     * and the initialization is NOT executed anymore.
     * In case the initialization has failed with any {@link Exception}
     * an {@link InitializationFailedException} is thrown and the {@link Lazy} is considered to be
     * not initialized.
     * Subsequent call of this method after failure will execution initialization again.
     * This can be repeated until the initialization is finally successful.
     *
     * @return initialized value (in case of initialization success)
     * @throws InitializationFailedException wrapping {@link Exception} that occurred during initialization failure
     */
    @Override
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

    /**
     * Creates a derived {@link Lazy} value by mapping currently wrapped value into a new one.
     * Call to this operation does not change the state of this {@link Lazy} value, so if it was not initialized before
     * it stays un-initialized. If it was initialized it stays initialized.
     *
     * @param mapper function to map the wrapped value into a new one
     * @param <T2>   the type of generated value
     * @return new {@link Lazy} value generated via the mapping function
     */
    public <T2> Lazy<T2> map(Function<? super T, ? extends T2> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper must be defined");
        }

        return new Lazy<>(() -> mapper.apply(this.value()));
    }
}
