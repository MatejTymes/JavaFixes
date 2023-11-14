package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class DoNotReplaceValueWithFailureRule<T> implements ValueReplacementRule<T> {

    private final ValueReplacementRule<T> otherwiseCheck;

    public DoNotReplaceValueWithFailureRule(ValueReplacementRule<T> otherwiseCheck) {
        assertNotNull(otherwiseCheck, "otherwiseCheck", "DoNotReplaceValueWithFailure");

        this.otherwiseCheck = otherwiseCheck;
    }

    @Override
    public boolean shouldReplaceOldValue(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        if (newValue.isFailure() && oldValue.isNotFailure()) {
            return false;
        }
        return otherwiseCheck.shouldReplaceOldValue(oldValue, newValue);
    }
}
