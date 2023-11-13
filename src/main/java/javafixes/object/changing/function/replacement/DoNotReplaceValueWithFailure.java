package javafixes.object.changing.function.replacement;

import javafixes.object.changing.FailableValue;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class DoNotReplaceValueWithFailure<T> implements ReplaceOldValueIf<T> {

    private final ReplaceOldValueIf<T> otherwiseCheck;

    public DoNotReplaceValueWithFailure(ReplaceOldValueIf<T> otherwiseCheck) {
        assertNotNull(otherwiseCheck, "otherwiseCheck", "DoNotReplaceValueWithFailure");

        this.otherwiseCheck = otherwiseCheck;
    }

    @Override
    public boolean replaceOldValueIf(FailableValue<? extends T> oldValue, FailableValue<? extends T> newValue) {
        if (newValue.isFailure() && oldValue.isNotFailure()) {
            return false;
        }
        return otherwiseCheck.replaceOldValueIf(oldValue, newValue);
    }
}
