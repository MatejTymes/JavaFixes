package javafixes.experimental.change.function;

import javafixes.object.Either;

import java.util.Objects;

public class EqualsBasedUseNewValueCheck<T> implements UseNewValueCheck<T> {

    private static EqualsBasedUseNewValueCheck<Objects> INSTANCE = new EqualsBasedUseNewValueCheck<>();

    public static <T> UseNewValueCheck<T> equalsBasedChecker() {
        return (EqualsBasedUseNewValueCheck) INSTANCE;
    }

    @Override
    public boolean useNewValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
