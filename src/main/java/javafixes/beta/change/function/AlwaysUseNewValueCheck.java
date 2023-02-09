package javafixes.beta.change.function;

import javafixes.object.Either;

import java.util.Objects;

public class AlwaysUseNewValueCheck<T> implements UseNewValueCheck<T> {

    private static AlwaysUseNewValueCheck<Objects> INSTANCE = new AlwaysUseNewValueCheck<>();

    public static <T> AlwaysUseNewValueCheck<T> alwaysUseNewValueCheck() {
        return (AlwaysUseNewValueCheck) INSTANCE;
    }

    @Override
    public boolean useNewValue(Either<RuntimeException, T> oldValue, Either<RuntimeException, T> newValue) {
        return true;
    }
}
