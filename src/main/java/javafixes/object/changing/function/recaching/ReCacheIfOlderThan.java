package javafixes.object.changing.function.recaching;

import javafixes.object.changing.FailableValue;

import java.time.Duration;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class ReCacheIfOlderThan<T> implements ReCacheValueIf<T> {

    public final Duration staleAfterDuration;

    public ReCacheIfOlderThan(
            Duration staleAfterDuration
    ) {
        assertNotNull(staleAfterDuration, "staleAfterDuration", this.getClass());

        this.staleAfterDuration = staleAfterDuration;
    }

    @Override
    public boolean reCacheValueIf(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        long ageInMS = System.currentTimeMillis() - lastRetrievalOfSourceValueTimestamp;
        return ageInMS > staleAfterDuration.toMillis();
    }
}
