package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class AlwaysReCacheValue<T> implements ReCacheValueCheck<T> {

    private static AlwaysReCacheValue<Objects> INSTANCE = new AlwaysReCacheValue<>();

    public static <T> AlwaysReCacheValue<T> alwaysReCacheValue() {
        return (AlwaysReCacheValue) INSTANCE;
    }
    @Override
    public boolean reCacheValue(FailableValue<T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        return true;
    }
}
