package javafixes.beta.change.function;

import javafixes.beta.change.FailableValue;

import java.util.ArrayList;
import java.util.List;

public class AnyOfReCachedValueCheck<T> implements ReCacheValueCheck<T> {

    private final List<ReCacheValueCheck<T>> checks;

    public AnyOfReCachedValueCheck(ReCacheValueCheck<T>... checks) {
        List<ReCacheValueCheck<T>> wrappedChecks = new ArrayList<>();

        for (ReCacheValueCheck<T> check : checks) {
            if (check instanceof AnyOfReCachedValueCheck) {
                wrappedChecks.addAll(((AnyOfReCachedValueCheck<T>) check).checks);
            } else {
                wrappedChecks.add(check);
            }
        }

        this.checks = wrappedChecks;
    }

    @Override
    public boolean reCacheValue(FailableValue<T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        for (ReCacheValueCheck<T> check : checks) {
            if (check.reCacheValue(currentValue, lastRetrievalOfSourceValueTimestamp)) {
                return true;
            }
        }
        return false;
    }
}
