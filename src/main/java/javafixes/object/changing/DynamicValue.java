package javafixes.object.changing;

import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
// todo: add toString()
public class DynamicValue<T> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicValue.class);

    private final Optional<String> valueName;
    private final Supplier<T> valueGenerator;
    private final Optional<Consumer<T>> onValueSetFunction;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long changeVersion;

    DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            Optional<Consumer<T>> onValueSetFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.onValueSetFunction = onValueSetFunction;
        this.disposeFunction = disposeFunction;
        this.changeVersion = 0;

        try {
            currentValue.set(right(valueGenerator.get()));
        } catch (RuntimeException e) {
            currentValue.set(left(e));
        }
    }

    private DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            Optional<Consumer<T>> onValueSetFunction,
            Optional<Consumer<T>> disposeFunction,

            Either<RuntimeException, T> currentValue,
            long changeVersion
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.onValueSetFunction = onValueSetFunction;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
        this.changeVersion = changeVersion;
    }

    public static <T> DynamicValue<T> dynamicValue(Supplier<T> valueGenerator) {
        return new DynamicValue<>(
                Optional.empty(),
                valueGenerator,
                Optional.empty(),
                Optional.empty()
        );
    }

    public DynamicValue<T> withValueName(String valueName) {
        synchronized (currentValue) {
            return new DynamicValue<>(
                    Optional.of(valueName),
                    valueGenerator,
                    onValueSetFunction,
                    disposeFunction,
                    currentValue.get(),
                    changeVersion
            );
        }
    }

    public DynamicValue<T> withNoValueName() {
        synchronized (currentValue) {
            return new DynamicValue<>(
                    Optional.empty(),
                    valueGenerator,
                    onValueSetFunction,
                    disposeFunction,
                    currentValue.get(),
                    changeVersion
            );
        }
    }

    public DynamicValue<T> withValueName(Optional<String> optionalValueName) {
        if (optionalValueName.isPresent()) {
            return withValueName(optionalValueName.get());
        } else {
            return withNoValueName();
        }
    }

    public DynamicValue<T> withOnValueSetFunction(Consumer<T> onValueSetFunction, boolean applyToCurrentValue) {
        synchronized (currentValue) {
            DynamicValue<T> dynamicValue = new DynamicValue<>(
                    valueName,
                    valueGenerator,
                    Optional.of(onValueSetFunction),
                    disposeFunction,
                    currentValue.get(),
                    changeVersion
            );

            if (applyToCurrentValue) {
                dynamicValue.applyOnValueSetFunction();
            }

            return dynamicValue;
        }
    }

    public DynamicValue<T> withNoOnValueSetFunction() {
        synchronized (currentValue) {
            return new DynamicValue<>(
                    valueName,
                    valueGenerator,
                    Optional.empty(),
                    disposeFunction,
                    currentValue.get(),
                    changeVersion
            );
        }
    }

    public DynamicValue<T> withOnValueSetFunction(Optional<Consumer<T>> optionalOnValueSetFunction, boolean applyToCurrentValue) {
        if (optionalOnValueSetFunction.isPresent()) {
            return withOnValueSetFunction(optionalOnValueSetFunction.get(), applyToCurrentValue);
        } else {
            return withNoOnValueSetFunction();
        }
    }

    public DynamicValue<T> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new DynamicValue<>(
                    valueName,
                    valueGenerator,
                    onValueSetFunction,
                    Optional.of(disposeFunction),
                    currentValue.get(),
                    changeVersion
            );
        }
    }

    public DynamicValue<T> withNoDisposeFunction() {
        synchronized (currentValue) {
            return new DynamicValue<>(
                    valueName,
                    valueGenerator,
                    onValueSetFunction,
                    Optional.empty(),
                    currentValue.get(),
                    changeVersion
            );
        }
    }

    public DynamicValue<T> withDisposeFunction(Optional<Consumer<T>> optionalDisposeFunction) {
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

    @Override
    public T value() {
        synchronized (currentValue) {
            reEvaluteValue();
            return currentValue.get()
                    .ifLeftThrow(e -> e)
                    .getRight();
        }
    }

    @Override
    public long changeVersion() {
        synchronized (currentValue) {
            reEvaluteValue();
            return changeVersion;
        }
    }

    private void reEvaluteValue() {
        Either<RuntimeException, T> oldValue = currentValue.get();

        try {
            T generatedValue = valueGenerator.get();
            updateValueIfDifferent(generatedValue);
        } catch (RuntimeException e) {
            updateAsFailure(e);
        }
    }

    private void updateValueIfDifferent(T potentialNewValue) {
        boolean shouldUpdate = currentValue.get().fold(
                ifException -> true,
                oldValue -> !Objects.equals(oldValue, potentialNewValue)
        );
        if (shouldUpdate) {
            updateTo(right(potentialNewValue));
        }
    }

    private void updateAsFailure(RuntimeException exception) {
        boolean shouldUpdate = currentValue.get().fold(
                oldException -> {
                    if (!Objects.equals(oldException.getClass(), exception.getClass())) {
                        return true;
                    } else if (!Objects.equals(oldException.getMessage(), exception.getMessage())) {
                        return true;
                    }
                    return false;
                },
                ifHasValue -> true
        );

        if (shouldUpdate) {
            updateTo(left(exception));
        }
    }

    private void updateTo(Either<RuntimeException, T> newValue) {
        Either<RuntimeException, T> oldValue = currentValue.getAndSet(newValue);

        changeVersion++;

        applyOnValueSetFunction();

        applyDisposeFunction(oldValue);
    }

    private void applyOnValueSetFunction() {
        try {
            onValueSetFunction.ifPresent(onValueSetFunction -> {
                currentValue.get().handleRight(onValueSetFunction::accept);
            });
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to apply onValueSetFunction to new value" + name().map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
        }
    }

    private void applyDisposeFunction(Either<RuntimeException, T> oldValue) {
        try {
            disposeFunction.ifPresent(disposeFunction -> {
                oldValue.handleRight(disposeFunction::accept);
            });
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to dispose old value" + name().map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
        }
    }
}
