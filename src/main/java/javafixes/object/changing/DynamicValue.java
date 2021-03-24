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
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long changeVersion;

    DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
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
            Optional<Consumer<T>> disposeFunction,

            Either<RuntimeException, T> currentValue,
            long changeVersion
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
        this.changeVersion = changeVersion;
    }

    public static <T> DynamicValue<T> dynamicValue(Supplier<T> valueGenerator) {
        return new DynamicValue<>(Optional.empty(), valueGenerator, Optional.empty());
    }

    public DynamicValue<T> withValueName(String valueName) {
        synchronized (currentValue) {
            return new DynamicValue<>(Optional.of(valueName), valueGenerator, disposeFunction, currentValue.get(), changeVersion);
        }
    }

    public DynamicValue<T> withNoValueName() {
        synchronized (currentValue) {
            return new DynamicValue<>(Optional.empty(), valueGenerator, disposeFunction, currentValue.get(), changeVersion);
        }
    }

    public DynamicValue<T> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new DynamicValue<>(valueName, valueGenerator, Optional.of(disposeFunction), currentValue.get(), changeVersion);
        }
    }

    public DynamicValue<T> withNoDisposeFunction() {
        synchronized (currentValue) {
            return new DynamicValue<>(valueName, valueGenerator, Optional.empty(), currentValue.get(), changeVersion);
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

    // todo: implement in better way
    public void updateAsFailure(RuntimeException exception) {
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

        try {
            disposeFunction.ifPresent(disposeFunction -> {
                oldValue.handleRight(disposeFunction::accept);
            });
        } catch (Exception loggableException) {
            logger.error("Failed to dispose old value", loggableException);
        }
    }
}