package javafixes.object;

import java.util.concurrent.Callable;

public class Lazy<T> {

    private T value = null;
    private boolean isInitialized = false;

    private final Callable<T> valueProvider;

    public Lazy(Callable<T> valueProvider) {
        this.valueProvider = valueProvider;
    }

    public static <T> Lazy<T> lazy(Callable<T> valueProvider) {
        return new Lazy<>(valueProvider);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public T value() {
        if (!isInitialized) {
            synchronized (this) {
                if (!isInitialized) {
                    try {
                        value = valueProvider.call();
                        isInitialized = true;
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to initialize Lazy value", e);
                    }
                }
            }
        }

        return value;
    }
}
