package javafixes.object.changing.function.valueHandler;

import javafixes.object.changing.FailableValue;

import java.util.Optional;

@FunctionalInterface
public interface EachValueHandler<T> {

    void handleEachValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value);

    // todo: mtymes - add and() function
//    default <T> EachValueHandler<T> and(EachValueHandler)
}
