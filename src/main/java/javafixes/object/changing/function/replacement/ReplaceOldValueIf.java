package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

@FunctionalInterface
public interface ReplaceOldValueIf<T> {

    boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);


    static <T> ReplaceOldValueIf<T> alwaysReplaceOldValue() {
        return (AlwaysReplaceOldValue) AlwaysReplaceOldValue.INSTANCE;
    }

    static <T> ReplaceOldValueIf<T> replaceNonEqualOldValue() {
        return (ReplaceNonEqualOldValue) ReplaceNonEqualOldValue.INSTANCE;
    }

    static <T> ReplaceOldValueIf<T> doNotReplaceValueWithFailureBut(ReplaceOldValueIf<T> otherwiseCheck) {
        return new DoNotReplaceValueWithFailure<>(otherwiseCheck);
    }

    static <T> ReplaceOldValueIf<T> doNotReplaceValueWithFailureOrEqualValue() {
        return doNotReplaceValueWithFailureBut(replaceNonEqualOldValue());
    }
}
