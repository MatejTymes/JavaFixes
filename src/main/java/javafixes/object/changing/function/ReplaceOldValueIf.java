package javafixes.object.changing.function;

import javafixes.object.changing.FailableValue;

@FunctionalInterface
public interface ReplaceOldValueIf<T> {

    boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);
}
