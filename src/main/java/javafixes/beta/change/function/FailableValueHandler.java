package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

@FunctionalInterface
public interface FailableValueHandler<T> {

    void handle(FailableValue<? extends T> value);
}
