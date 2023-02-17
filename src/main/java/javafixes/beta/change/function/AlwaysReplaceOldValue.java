package javafixes.beta.change.function;

import javafixes.object.Either;

import java.util.Objects;

public class AlwaysReplaceOldValue<T> implements ShouldReplaceOldValueCheck<T> {

    private static AlwaysReplaceOldValue<Objects> INSTANCE = new AlwaysReplaceOldValue<>();

    public static <T> AlwaysReplaceOldValue<T> alwaysReplaceOldValue() {
        return (AlwaysReplaceOldValue) INSTANCE;
    }

    @Override
    public boolean shouldReplaceOldValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue) {
        return true;
    }
}
