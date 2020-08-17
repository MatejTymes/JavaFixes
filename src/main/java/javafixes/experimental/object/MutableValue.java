package javafixes.experimental.object;

import javafixes.object.Either;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
// todo: add toString()
public class MutableValue<T> implements DynamicValue<T> {

    private final Optional<String> valueName;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long valueVersion;

    MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> currentValue,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
        valueVersion = 0;
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
    public long valueVersion() {
        return valueVersion;
    }

    private void updateTo(Either<RuntimeException, T> newValue) {
        Either<RuntimeException, T> oldValue = currentValue.getAndSet(newValue);

        valueVersion++;

        disposeFunction.ifPresent(disposeFunction -> {
            oldValue.handleRight(disposeFunction::accept);
        });
    }
}
