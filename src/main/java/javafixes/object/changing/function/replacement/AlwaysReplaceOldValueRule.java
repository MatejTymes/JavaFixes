package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

import java.util.Objects;

public class AlwaysReplaceOldValueRule<T> implements ValueReplacementRule<T> {

    public static AlwaysReplaceOldValueRule<Objects> INSTANCE = new AlwaysReplaceOldValueRule<>();

    @Override
    public boolean shouldReplaceOldValue(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        return true;
    }
}
