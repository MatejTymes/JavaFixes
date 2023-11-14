package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

import java.util.Objects;

public class ReplaceNonEqualOldValueRule<T> implements ValueReplacementRule<T> {

    public static ReplaceNonEqualOldValueRule<Objects> INSTANCE = new ReplaceNonEqualOldValueRule<>();

    @Override
    public boolean shouldReplaceOldValue(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
