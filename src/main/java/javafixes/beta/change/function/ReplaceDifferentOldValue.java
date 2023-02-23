package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class ReplaceDifferentOldValue<T> implements ReplaceOldValueIf<T> {

    private static ReplaceDifferentOldValue<Objects> INSTANCE = new ReplaceDifferentOldValue<>();

    public static <T> ReplaceOldValueIf<T> replaceDifferentOldValue() {
        return (ReplaceDifferentOldValue) INSTANCE;
    }

    @Override
    public boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
