package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

import java.util.Objects;

public class AlwaysReplaceOldValue<T> implements ReplaceOldValueIf<T> {

    public static AlwaysReplaceOldValue<Objects> INSTANCE = new AlwaysReplaceOldValue<>();

    @Override
    public boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        return true;
    }
}
