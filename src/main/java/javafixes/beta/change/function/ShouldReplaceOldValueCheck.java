package javafixes.beta.change.function;

import javafixes.object.Either;

@FunctionalInterface
public interface ShouldReplaceOldValueCheck<T> {

    boolean shouldReplaceOldValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue);
}
