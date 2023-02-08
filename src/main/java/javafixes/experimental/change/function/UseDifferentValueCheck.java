package javafixes.experimental.change.function;

import javafixes.object.Either;

import java.util.Objects;

public class UseDifferentValueCheck<T> implements UseNewValueCheck<T> {

    private static UseDifferentValueCheck<Objects> INSTANCE = new UseDifferentValueCheck<>();

    public static <T> UseNewValueCheck<T> equalsBasedChecker() {
        return (UseDifferentValueCheck) INSTANCE;
    }

    @Override
    public boolean useNewValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
