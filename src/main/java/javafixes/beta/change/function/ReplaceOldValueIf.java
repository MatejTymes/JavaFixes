package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

@FunctionalInterface
public interface ReplaceOldValueIf<T> {

    boolean replaceOldValueIf(FailableValue<T> oldValue, FailableValue<T> newValue);
}
