package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class ReCacheIfFailure<T> implements ReCacheValueCheck<T> {

    public static ReCacheIfFailure<Objects> INSTANCE = new ReCacheIfFailure<>();

    @Override
    public boolean reCacheValue(FailableValue<T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        return currentValue.isFailure();
    }
}
