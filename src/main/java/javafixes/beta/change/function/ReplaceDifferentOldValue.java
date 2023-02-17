package javafixes.beta.change.function;

import javafixes.object.Either;

import java.util.Objects;

public class ReplaceDifferentOldValue<T> implements ShouldReplaceOldValueCheck<T> {

    private static ReplaceDifferentOldValue<Objects> INSTANCE = new ReplaceDifferentOldValue<>();

    public static <T> ShouldReplaceOldValueCheck<T> replaceDifferentOldValue() {
        return (ReplaceDifferentOldValue) INSTANCE;
    }

    @Override
    public boolean shouldReplaceOldValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
