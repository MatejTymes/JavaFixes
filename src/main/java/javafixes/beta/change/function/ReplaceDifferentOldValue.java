package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.Objects;

public class ReplaceDifferentOldValue<T> implements ReplaceOldValueCheck<T> {

    private static ReplaceDifferentOldValue<Objects> INSTANCE = new ReplaceDifferentOldValue<>();

    public static <T> ReplaceOldValueCheck<T> replaceDifferentOldValue() {
        return (ReplaceDifferentOldValue) INSTANCE;
    }

    @Override
    public boolean shouldReplaceOldValue(FailableValue<T> oldValue, FailableValue<T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
