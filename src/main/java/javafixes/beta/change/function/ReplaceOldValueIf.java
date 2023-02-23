package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

@FunctionalInterface
public interface ReplaceOldValueIf<T> {

    boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);
}
