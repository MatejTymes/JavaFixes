package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Optional;

@FunctionalInterface
public interface EachValueHandler<T> {

    void handleEachValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value);
}
