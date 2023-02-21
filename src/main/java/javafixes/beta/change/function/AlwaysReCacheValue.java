package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class AlwaysReCacheValue<T> implements ReCacheValueIf<T> {

    public static AlwaysReCacheValue<Objects> INSTANCE = new AlwaysReCacheValue<>();

    @Override
    public boolean reCacheValueIf(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        return true;
    }
}
