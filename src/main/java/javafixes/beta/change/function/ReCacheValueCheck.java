package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.time.Duration;

public interface ReCacheValueCheck<T> {

    boolean reCacheValue(FailableValue<T> currentValue, long lastRetrievalOfSourceValueTimestamp);

    default ReCacheValueCheck<T> or(ReCacheValueCheck<T> anotherCheck) {
        return new AnyOfReCachedValueCheck<>(this, anotherCheck);
    }

    static <T> ReCacheValueCheck<T> reCacheIfOlderThan(Duration staleAfterDuration) {
        return new ReCacheIfOlderThan<>(staleAfterDuration);
    }

    static <T> ReCacheIfFailure<T> reCacheIfFailure() {
        return (ReCacheIfFailure) ReCacheIfFailure.INSTANCE;
    }

    static <T> AlwaysReCacheValue<T> alwaysReCacheValue() {
        return (AlwaysReCacheValue) AlwaysReCacheValue.INSTANCE;
    }
}
