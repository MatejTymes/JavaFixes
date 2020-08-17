package javafixes.object;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
public class MutableValue<T> implements DynamicValue<T> {

    private final Optional<String> valueName;
    private final AtomicReference<Either<RuntimeException, T>> value = new AtomicReference<>();
    private long valueVersion;

    public MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> value
    ) {
        this.valueName = valueName;
        this.value.set(value);
        valueVersion = 0;
    }

    public static <T> MutableValue<T> mutableValue(T value) {
        return new MutableValue<>(Optional.empty(), right(value));
    }

    public static <T> MutableValue<T> mutableValue(String valueName, T value) {
        return new MutableValue<>(Optional.of(valueName), right(value));
    }

    public static <T> MutableValue<T> failedMutableValue(RuntimeException e) {
        return new MutableValue<>(Optional.empty(), left(e));
    }

    public static <T> MutableValue<T> failedMutableValue(String valueName, RuntimeException e) {
        return new MutableValue<>(Optional.of(valueName), left(e));
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    public void updateValue(T newValue) {
        synchronized (value) {
            value.set(right(newValue));
            valueVersion++;
        }
    }

    public void updateAsFailure(RuntimeException exception) {
        synchronized (value) {
            value.set(left(exception));
            valueVersion++;
        }
    }

    @Override
    public T value() {
        return value.get()
                .ifLeftThrow(e -> e)
                .getRight();
    }

    @Override
    public long valueVersion() {
        return valueVersion;
    }

    @Override
    public <T2> DynamicValue<T2> map(Function<T, ? extends T2> mapper) {
        synchronized (value) {
            return new DerivedValue<>(Optional.empty(), this, mapper);
        }
    }

    @Override
    public <T2> DynamicValue<T2> map(String derivedValueName, Function<T, ? extends T2> mapper) {
        synchronized (value) {
            return new DerivedValue<>(Optional.of(derivedValueName), this, mapper);
        }
    }
}
