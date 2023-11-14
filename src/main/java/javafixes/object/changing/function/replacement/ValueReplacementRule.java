package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

@FunctionalInterface
public interface ValueReplacementRule<T> {

    boolean shouldReplaceOldValue(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue);


    static <T> ValueReplacementRule<T> alwaysReplaceOldValue() {
        return (AlwaysReplaceOldValueRule) AlwaysReplaceOldValueRule.INSTANCE;
    }

    static <T> ValueReplacementRule<T> replaceNonEqualOldValue() {
        return (ReplaceNonEqualOldValueRule) ReplaceNonEqualOldValueRule.INSTANCE;
    }

    static <T> ValueReplacementRule<T> doNotReplaceValueWithFailureBut(ValueReplacementRule<T> otherwiseCheck) {
        return new DoNotReplaceValueWithFailureRule<>(otherwiseCheck);
    }

    static <T> ValueReplacementRule<T> doNotReplaceValueWithEqualValueOrWithFailure() {
        return doNotReplaceValueWithFailureBut(replaceNonEqualOldValue());
    }
}
