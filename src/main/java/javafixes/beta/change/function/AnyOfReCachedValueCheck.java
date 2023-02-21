package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.ArrayList;
import java.util.List;

public class AnyOfReCachedValueCheck<T> implements ReCacheValueCheck<T> {

    private final List<ReCacheValueCheck<? super T>> checks;

    public AnyOfReCachedValueCheck(ReCacheValueCheck<? super T>... checks) {
        List<ReCacheValueCheck<? super T>> wrappedChecks = new ArrayList<>();

        for (ReCacheValueCheck<? super T> check : checks) {
            if (check instanceof AnyOfReCachedValueCheck) {
                wrappedChecks.addAll(((AnyOfReCachedValueCheck<? super T>) check).checks);
            } else {
                wrappedChecks.add(check);
            }
        }

        this.checks = wrappedChecks;
    }

    @Override
    public boolean reCacheValue(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        for (ReCacheValueCheck<? super T> check : checks) {
            if (check.reCacheValue(currentValue, lastRetrievalOfSourceValueTimestamp)) {
                return true;
            }
        }
        return false;
    }
}
