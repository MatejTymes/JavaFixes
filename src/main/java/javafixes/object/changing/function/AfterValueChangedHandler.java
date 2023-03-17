package javafixes.object.changing.function;

import javafixes.object.changing.FailableValue;

@FunctionalInterface
public interface AfterValueChangedHandler<T> {

    void afterValueChanged(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);
}
