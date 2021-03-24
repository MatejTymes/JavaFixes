package javafixes.object.changing;

import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
// todo: add toString()
public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(MutableValue.class);

    private final Optional<String> valueName;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long changeVersion;

    MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> currentValue,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
        changeVersion = 0;
    }

    private MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> currentValue,
            Optional<Consumer<T>> disposeFunction,
            long changeVersion
    ) {
        this.valueName = valueName;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
        this.changeVersion = changeVersion;
    }

    public static <T> MutableValue<T> mutableValue(T initialValue) {
        return new MutableValue<>(Optional.empty(), right(initialValue), Optional.empty());
    }

    public MutableValue<T> withValueName(String valueName) {
        synchronized (currentValue) {
            return new MutableValue<>(Optional.of(valueName), currentValue.get(), disposeFunction, changeVersion);
        }
    }

    public MutableValue<T> withNoValueName() {
        synchronized (currentValue) {
            return new MutableValue<>(Optional.empty(), currentValue.get(), disposeFunction, changeVersion);
        }
    }

    public MutableValue<T> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new MutableValue<>(valueName, currentValue.get(), Optional.of(disposeFunction), changeVersion);
        }
    }

    public MutableValue<T> withNoDisposeFunction() {
        synchronized (currentValue) {
            return new MutableValue<>(valueName, currentValue.get(), Optional.empty(), changeVersion);
        }
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    public void updateValue(T newValue) {
        synchronized (currentValue) {
            updateTo(right(newValue));
        }
    }

    public void updateValueIfDifferent(T potentialNewValue) {
        synchronized (currentValue) {
            boolean shouldUpdate = currentValue.get().fold(
                    ifException -> true,
                    oldValue -> !Objects.equals(oldValue, potentialNewValue)
            );
            if (shouldUpdate) {
                updateTo(right(potentialNewValue));
            }
        }
    }

    public void updateAsFailure(RuntimeException exception) {
        synchronized (currentValue) {
            updateTo(left(exception));
        }
    }

    @Override
    public T value() {
        return currentValue.get()
                .ifLeftThrow(e -> e)
                .getRight();
    }

    @Override
    public long changeVersion() {
        return changeVersion;
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
