package javafixes.object.changing.function.recaching;

import javafixes.object.changing.FailableValue;

import java.util.ArrayList;
import java.util.List;

public class ReCacheAnyValue<T> implements ReCacheValueIf<T> {

    private final List<ReCacheValueIf<? super T>> checks;

    public ReCacheAnyValue(ReCacheValueIf<? super T>... checks) {
        List<ReCacheValueIf<? super T>> wrappedChecks = new ArrayList<>();

        for (ReCacheValueIf<? super T> check : checks) {
            if (check instanceof ReCacheAnyValue) {
                wrappedChecks.addAll(((ReCacheAnyValue<? super T>) check).checks);
            } else {
                wrappedChecks.add(check);
            }
        }

        this.checks = wrappedChecks;
    }

    @Override
    public boolean reCacheValueIf(FailableValue<? extends T> currentValue, long lastRetrievalOfSourceValueTimestamp) {
        for (ReCacheValueIf<? super T> check : checks) {
            if (check.reCacheValueIf(currentValue, lastRetrievalOfSourceValueTimestamp)) {
                return true;
            }
        }
        return false;
    }
}
