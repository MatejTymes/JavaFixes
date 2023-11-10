package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

import java.util.Objects;

public class ReplaceNonEqualOldValue<T> implements ReplaceOldValueIf<T> {

    public static ReplaceNonEqualOldValue<Objects> INSTANCE = new ReplaceNonEqualOldValue<>();

    @Override
    public boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
