package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class AlwaysReplaceOldValue<T> implements ReplaceOldValueIf<T> {

    private static AlwaysReplaceOldValue<Objects> INSTANCE = new AlwaysReplaceOldValue<>();

    public static <T> AlwaysReplaceOldValue<T> alwaysReplaceOldValue() {
        return (AlwaysReplaceOldValue) INSTANCE;
    }

    @Override
    public boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        return true;
    }
}
