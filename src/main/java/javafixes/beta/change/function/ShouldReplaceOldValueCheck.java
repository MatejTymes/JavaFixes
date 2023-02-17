package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

@FunctionalInterface
public interface ShouldReplaceOldValueCheck<T> {

    boolean shouldReplaceOldValue(FailableValue<T> oldValue, FailableValue<T> newValue);
}
