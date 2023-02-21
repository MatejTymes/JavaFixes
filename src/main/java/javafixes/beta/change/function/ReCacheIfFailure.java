package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class ReCacheIfFailure<T> implements ReCacheValueIf<T> {

    public static ReCacheIfFailure<Objects> INSTANCE = new ReCacheIfFailure<>();

    @Override
    public boolean reCacheValueIf(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        return currentValue.isFailure();
    }
}
