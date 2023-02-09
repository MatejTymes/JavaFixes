package javafixes.beta.change.function;

import javafixes.object.Either;

@FunctionalInterface
public interface UseNewValueCheck<T> {

    boolean useNewValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue);
}
