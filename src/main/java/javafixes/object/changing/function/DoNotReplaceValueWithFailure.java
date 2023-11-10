package javafixes.object.changing.function;

import javafixes.object.changing.FailableValue;

public class DoNotReplaceValueWithFailure<T> implements ReplaceOldValueIf<T> {

    private final ReplaceOldValueIf<T> otherwiseCheck;

    public DoNotReplaceValueWithFailure(ReplaceOldValueIf<T> otherwiseCheck) {
        this.otherwiseCheck = otherwiseCheck;
    }

    public static <T> DoNotReplaceValueWithFailure<T> doNotReplaceValueWithFailureBut(
            ReplaceOldValueIf<T> otherwiseCheck
    ) {
        return new DoNotReplaceValueWithFailure<>(otherwiseCheck);
    }

    @Override
    public boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        if (newValue.isFailure() && oldValue.isNotFailure()) {
            return otherwiseCheck.replaceOldValueIf(oldValue, newValue);
        }
        return true;
    }
}
