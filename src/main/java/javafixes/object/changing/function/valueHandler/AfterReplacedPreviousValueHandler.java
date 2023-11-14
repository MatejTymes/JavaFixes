package javafixes.object.changing.function.valueHandler;

import javafixes.object.changing.FailableValue;

@FunctionalInterface
public interface AfterReplacedPreviousValueHandler<T> {

    void afterValueChanged(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);
}
