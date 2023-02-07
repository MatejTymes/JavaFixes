package javafixes.object.changing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;
import static javafixes.object.changing.ChangingValueUtil.applyDisposeFunction;
import static javafixes.object.changing.ChangingValueUtil.applyOnValueChangedFunction;
import static javafixes.object.changing.VersionedValue.versionedValue;

// todo: test
// todo: javadoc
// todo: add toString()
public class PeriodicallyReCachedValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicallyReCachedValue.class);

    private final Optional<String> valueName;
    private final Optional<Consumer<T>> onValueChangedFunction;
    private final Optional<Consumer<T>> disposeFunction;


    private final ChangingValue<T> sourceValue;
    private final AtomicReference<VersionedValue<T>> currentCachedValue = new AtomicReference<>();
    private final AtomicLong lastCheckOn = new AtomicLong();


    // todo: mtymes - define if the value should be populated immediately or not
    // todo: mtymes - add the ability to do it in background or on call
    // todo: mtymes - add flag to not updateIf new value is exception
    // todo: mtymes - add flag to re-evaluate if last value is exception
    PeriodicallyReCachedValue(
            Optional<String> valueName,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<Consumer<T>> disposeFunction,
            ChangingValue<T> sourceValue,
            Duration refreshPeriod,
            ScheduledExecutorService usingExecutor
    ) {

        this.valueName = valueName;
        this.onValueChangedFunction = onValueChangedFunction;
        this.disposeFunction = disposeFunction;

        this.sourceValue = sourceValue;
        try {
            this.currentCachedValue.set(versionedValue(right(sourceValue.value())));
        } catch (RuntimeException e) {
            this.currentCachedValue.set(versionedValue(left(e)));
        }
        this.lastCheckOn.set(System.currentTimeMillis());

        usingExecutor.scheduleAtFixedRate(
                this::reEvaluteValue,
                refreshPeriod.toMillis(),
                refreshPeriod.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public T value() {
        return currentCachedValue.get().value();
    }

    @Override
    public long changeVersion() {
        return currentCachedValue.get().versionNumber;
    }

    public void forceNewValueReCaching() {
        reEvaluteValue();
    }

    private void reEvaluteValue() {
        try {
            T generatedValue = sourceValue.value();
            updateValueIfDifferent(generatedValue);
        } catch (RuntimeException e) {
            updateAsFailure(e);
        }
    }

    private void updateValueIfDifferent(T potentialNewValue) {
        synchronized (currentCachedValue) {
            long timestamp = System.currentTimeMillis();

            VersionedValue<T> nextValue = currentCachedValue.get().generateNextVersionIfDifferent(potentialNewValue);
            if (nextValue != null) {
                updateTo(
                        nextValue,
                        timestamp
                );
            } else {
                lastCheckOn.set(timestamp);
            }
        }
    }

    private void updateAsFailure(RuntimeException exception) {
        synchronized (currentCachedValue) {
            long timestamp = System.currentTimeMillis();

            VersionedValue<T> nextValue = currentCachedValue.get().generateNextVersionAsFailure(exception);
            updateTo(
                    nextValue,
                    timestamp
            );
        }
//        boolean shouldUpdate = currentCachedValue.get().fold(
//                oldException -> {
//                    if (!Objects.equals(oldException.getClass(), exception.getClass())) {
//                        return true;
//                    } else if (!Objects.equals(oldException.getMessage(), exception.getMessage())) {
//                        return true;
//                    }
//                    return false;
//                },
//                ifHasValue -> true
//        );
//
//        if (shouldUpdate) {
//            updateTo(left(exception));
//        }
    }

    private void updateTo(VersionedValue<T> newValue, long timestamp) {
        synchronized (currentCachedValue) {
            VersionedValue<T> oldValue = currentCachedValue.getAndSet(newValue);

            applyOnValueChangedFunction(
                    currentCachedValue.get().value,
                    onValueChangedFunction,
                    valueName,
                    logger
            );

            applyDisposeFunction(
                    oldValue.value,
                    disposeFunction,
                    valueName,
                    logger
            );

            lastCheckOn.set(timestamp);
        }
    }
}
