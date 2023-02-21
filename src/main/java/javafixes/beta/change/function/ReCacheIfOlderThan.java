package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.time.Duration;

public class ReCacheIfOlderThan<T> implements ReCacheValueIf<T> {

    public final Duration staleAfterDuration;

    public ReCacheIfOlderThan(
            Duration staleAfterDuration
    ) {
        this.staleAfterDuration = staleAfterDuration;
    }

    @Override
    public boolean reCacheValueIf(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        long ageInMS = System.currentTimeMillis() - lastRetrievalOfSourceValueTimestamp;
        return ageInMS > staleAfterDuration.toMillis();
    }
}
