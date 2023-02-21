package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class AlwaysReCacheValue<T> implements ReCacheValueCheck<T> {

    public static AlwaysReCacheValue<Objects> INSTANCE = new AlwaysReCacheValue<>();

    @Override
    public boolean reCacheValue(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        return true;
    }
}
