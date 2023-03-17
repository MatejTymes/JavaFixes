package javafixes.object.changing.function;

import javafixes.object.changing.FailableValue;

import java.time.Duration;

@FunctionalInterface
public interface ReCacheValueIf<T> {

    boolean reCacheValueIf(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp);

    default ReCacheValueIf<T> or(ReCacheValueIf<T> anotherCheck) {
        return new ReCacheAnyValue<>(this, anotherCheck);
    }

    static <T> ReCacheValueIf<T> reCacheIfOlderThan(Duration staleAfterDuration) {
        return new ReCacheIfOlderThan<>(staleAfterDuration);
    }

    static <T> ReCacheIfFailure<T> reCacheIfFailure() {
        return (ReCacheIfFailure) ReCacheIfFailure.INSTANCE;
    }

    static <T> AlwaysReCacheValue<T> alwaysReCacheValue() {
        return (AlwaysReCacheValue) AlwaysReCacheValue.INSTANCE;
    }
}
