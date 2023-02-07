package javafixes.object.changing;

import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;
import static javafixes.object.changing.ChangingValueUtil.applyDisposeFunction;
import static javafixes.object.changing.ChangingValueUtil.applyOnValueChangedFunction;
import static javafixes.object.changing.VersionedValue.versionedValue;

/**
 * {@code MutableValue} is a wrapper of value that you can replace and whose changes will be propagated
 * to derived values.
 * <p>
 * <p>A good usage example would be a config from which other values could be derived and should be changed
 * once config changes.
 *
 * @param <T> type of wrapped value
 * @author mtymes
 */
// todo: test properly
// todo: add toString()
public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(MutableValue.class);

    private final Optional<String> valueName;
    private final Optional<Consumer<T>> onValueChangedFunction;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<VersionedValue<T>> currentValue = new AtomicReference<>();

    MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> currentValue,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.onValueChangedFunction = onValueChangedFunction;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(versionedValue(currentValue));
    }

    private MutableValue(
            Optional<String> valueName,
            VersionedValue<T> currentValue,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.onValueChangedFunction = onValueChangedFunction;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
    }

    /**
     * Factory method for {@code MutableValue}
     *
     * @param initialValue initial value
     * @param <T>          type of wrapped value
     * @return {@code MutableValue} value
     */
    public static <T> MutableValue<T> mutableValue(T initialValue) {
        return new MutableValue<>(
                Optional.empty(),
                right(initialValue),
                Optional.empty(),
                Optional.empty());
    }

    /**
     * Factory method for {@code MutableValue}
     *
     * @param exception {@code RuntimeException} caught when trying to get initial value
     * @param <T>       type of wrapped value
     * @return {@code MutableValue} value
     */
    public static <T> MutableValue<T> failedMutableValue(RuntimeException exception) {
        return new MutableValue<>(
                Optional.empty(),
                left(exception),
                Optional.empty(),
                Optional.empty()
        );
    }

    /**
     * Creates a copy of current {@code MutableValue} with defined value name
     *
     * @param valueName new value name to use
     * @return copy of current {@code MutableValue} with a new value name
     */
    public MutableValue<T> withValueName(String valueName) {
        synchronized (currentValue) {
            return new MutableValue<>(
                    Optional.of(valueName),
                    currentValue.get(),
                    onValueChangedFunction,
                    disposeFunction
            );
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with value name being removed
     *
     * @return copy of current {@code MutableValue} without a value name
     */
    public MutableValue<T> withNoValueName() {
        synchronized (currentValue) {
            return new MutableValue<>(
                    Optional.empty(),
                    currentValue.get(),
                    onValueChangedFunction,
                    disposeFunction
            );
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with new value name if {@code Optional} name is defined
     * and without a value name if the {@code Optional} name is undefined
     *
     * @param optionalValueName if empty new {@code MutableValue} will have no value name otherwise the define value name will be used
     * @return copy of current {@code MutableValue} with defined value name
     */
    public MutableValue<T> withValueName(Optional<String> optionalValueName) {
        if (optionalValueName.isPresent()) {
            return withValueName(optionalValueName.get());
        } else {
            return withNoValueName();
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with defined function applied to each new wrapped value
     *
     * @param onValueChangedFunction function that is called when a wrapped value is changed
     * @param applyToCurrentValue    indicator if the {@code onValueChangedFunction} should be applied to the currently wrapped value
     * @return copy of current {@code MutableValue} with a new on value changed function
     */
    public MutableValue<T> withOnValueChangedFunction(Consumer<T> onValueChangedFunction, boolean applyToCurrentValue) {
        synchronized (currentValue) {
            MutableValue<T> mutableValue = new MutableValue<>(
                    valueName,
                    currentValue.get(),
                    Optional.of(onValueChangedFunction),
                    disposeFunction
            );

            if (applyToCurrentValue) {
                applyOnValueChangedFunction(
                        mutableValue.currentValue.get().value,
                        mutableValue.onValueChangedFunction,
                        mutableValue.valueName,
                        logger
                );
            }

            return mutableValue;
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with on value changed function being removed
     *
     * @return copy of current {@code MutableValue} without an on value changed function
     */
    public MutableValue<T> withNoOnValueChangedFunction() {
        synchronized (currentValue) {
            return new MutableValue<>(
                    valueName,
                    currentValue.get(),
                    Optional.empty(),
                    disposeFunction
            );
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with new on value changed function if {@code Optional} on value changed function is defined
     * and without an on value changed function if the {@code Optional} on value changed function is undefined
     *
     * @param optionalOnValueChangedFunction if empty new {@code MutableValue} will have no on value changed function otherwise the define on value changed function will be used
     * @param applyToCurrentValue            indicator if the defined {@code optionalOnValueChangedFunction} should be applied to the currently wrapped value
     * @return copy of current {@code MutableValue} with defined on value changed function
     */
    public MutableValue<T> withOnValueChangedFunction(Optional<Consumer<T>> optionalOnValueChangedFunction, boolean applyToCurrentValue) {
        if (optionalOnValueChangedFunction.isPresent()) {
            return withOnValueChangedFunction(optionalOnValueChangedFunction.get(), applyToCurrentValue);
        } else {
            return withNoOnValueChangedFunction();
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with defined dispose function
     *
     * @param disposeFunction function that is called using old value when a new value is being passed in
     * @return copy of current {@code MutableValue} with a new dispose function
     */
    public MutableValue<T> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new MutableValue<>(
                    valueName,
                    currentValue.get(),
                    onValueChangedFunction,
                    Optional.of(disposeFunction)
            );
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with dispose function being removed
     *
     * @return copy of current {@code MutableValue} without a dispose function
     */
    public MutableValue<T> withNoDisposeFunction() {
        synchronized (currentValue) {
            return new MutableValue<>(
                    valueName,
                    currentValue.get(),
                    onValueChangedFunction,
                    Optional.empty()
            );
        }
    }

    /**
     * Creates a copy of current {@code MutableValue} with new dispose function if {@code Optional} dispose function is defined
     * and without a dispose function if the {@code Optional} dispose function is undefined
     *
     * @param optionalDisposeFunction if empty new {@code MutableValue} will have no dispose function otherwise the define dispose function will be used
     * @return copy of current {@code MutableValue} with defined dispose function
     */
    public MutableValue<T> withDisposeFunction(Optional<Consumer<T>> optionalDisposeFunction) {
        if (optionalDisposeFunction.isPresent()) {
            return withDisposeFunction(optionalDisposeFunction.get());
        } else {
            return withNoDisposeFunction();
        }
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    /**
     * Updates wrapped value and increments the {@code changeVersion}
     *
     * @param newValue new value that will be wrapped
     */
    public void updateValue(T newValue) {
        synchronized (currentValue) {
            updateTo(currentValue.get().generateNextVersion(newValue));
        }
    }

    /**
     *
     * @param newValue new value that will be wrapped
     */
    /**
     * If {@code potentialNewValue} is different than the already wrapped, it then value will replace the old one
     * and the {@code changeVersion} will be incremented. If they will be the same then no change will happen
     *
     * @param potentialNewValue value that might be wrapped if it is different than the already wrapped
     * @return if the wrapped value has been changed
     */
    public boolean updateValueIfDifferent(T potentialNewValue) {
        synchronized (currentValue) {
            VersionedValue<T> nextValue = currentValue.get().generateNextVersionIfDifferent(potentialNewValue);

            if (nextValue == null) {
                return false;
            }

            updateTo(nextValue);

            return true;
        }
    }

    /**
     * Updates wrapped value as an exception and increments the {@code changeVersion}
     *
     * @param exception new value that will be wrapped
     */
    public void updateAsFailure(RuntimeException exception) {
        synchronized (currentValue) {
            updateTo(currentValue.get().generateNextVersionAsFailure(exception));
        }
    }

    @Override
    public T value() {
        return currentValue.get().value();
    }

    @Override
    public long changeVersion() {
        return currentValue.get().versionNumber;
    }

    private void updateTo(VersionedValue<T> newValue) {
        synchronized (currentValue) {
            VersionedValue<T> oldValue = currentValue.getAndSet(newValue);

            applyOnValueChangedFunction(
                    currentValue.get().value,
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
        }
    }
}
