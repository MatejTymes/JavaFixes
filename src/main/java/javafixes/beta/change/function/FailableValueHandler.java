package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Optional;

@FunctionalInterface
public interface FailableValueHandler<T> {

    void handle(Optional<String> valueName, FailableValue<? extends T> value);
}
