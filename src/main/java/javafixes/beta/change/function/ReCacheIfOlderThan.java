package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.time.Duration;

public class ReCacheIfOlderThan<T> implements ReCacheValueCheck<T> {

    public final Duration staleAfterDuration;

    public ReCacheIfOlderThan(
            Duration staleAfterDuration
    ) {
        this.staleAfterDuration = staleAfterDuration;
    }

    public static <T> ReCacheValueCheck<T> reCacheIfOlderThan(Duration staleAfterDuration) {
        return new ReCacheIfOlderThan<>(staleAfterDuration);
    }

    @Override
    public boolean reCacheValue(FailableValue<T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        long ageInMS = System.currentTimeMillis() - lastRetrievalOfSourceValueTimestamp;
        return ageInMS > staleAfterDuration.toMillis();
    }
}
