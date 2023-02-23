package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

@FunctionalInterface
public interface AfterValueChangedHandler<T> {

    void afterValueChanged(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);
}
