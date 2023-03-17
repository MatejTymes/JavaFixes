package javafixes.object.changing.function;

import javafixes.object.changing.FailableValue;

import java.util.Optional;

@FunctionalInterface
public interface EachValueHandler<T> {

    void handleEachValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value);
}
