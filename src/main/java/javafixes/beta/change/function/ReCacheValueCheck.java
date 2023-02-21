package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

public interface ReCacheValueCheck<T> {

    boolean reCacheValue(FailableValue<T> currentValue, long lastRetrievalOfSourceValueTimestamp);
}
