package javafixes.experimental.object;

import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
// todo: add toString()
public class DerivedValue<T, SourceValue> implements DynamicValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);

    private final Optional<String> valueName;
    private final DynamicValue<SourceValue> sourceValue;
    private final Function<SourceValue, ? extends T> valueMapper;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long valueVersion;
    private long lastSourceValueVersion;

    DerivedValue(
            Optional<String> valueName,
            DynamicValue<SourceValue> sourceValue,
            Function<SourceValue, ? extends T> valueMapper,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.disposeFunction = disposeFunction;

        deriveValue();
        this.valueVersion = 0;
    }

    private DerivedValue(
            Optional<String> valueName,
            DynamicValue<SourceValue> sourceValue,
            Function<SourceValue, ? extends T> valueMapper,
            Optional<Consumer<T>> disposeFunction,

            Either<RuntimeException, T> derivedValue
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(derivedValue);
        this.valueVersion = 0;
    }

    public DerivedValue<T, SourceValue> withValueName(String valueName) {
        synchronized (currentValue) {
            return new DerivedValue<>(Optional.of(valueName), sourceValue, valueMapper, disposeFunction, currentValue.get());
        }
    }

    public DerivedValue<T, SourceValue> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new DerivedValue<>(valueName, sourceValue, valueMapper, Optional.of(disposeFunction), currentValue.get());
        }
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public T value() {
        ensureIsDerivedFromLatestVersion();

        return currentValue.get()
                .ifLeftThrow(e -> e)
                .getRight();
    }

    @Override
    public long valueVersion() {
        ensureIsDerivedFromLatestVersion();

        return valueVersion;
    }

    private void ensureIsDerivedFromLatestVersion() {
        if (lastSourceValueVersion != sourceValue.valueVersion()) {
            synchronized (currentValue) {
                if (lastSourceValueVersion != sourceValue.valueVersion()) {
                    deriveValue();
                }
            }
        }
    }

    private void deriveValue() {
        this.lastSourceValueVersion = sourceValue.valueVersion();

        Either<RuntimeException, T> oldValue;
        try {
            oldValue = this.currentValue.getAndSet(right(valueMapper.apply(sourceValue.value())));
        } catch (RuntimeException e) {
            oldValue = this.currentValue.getAndSet(left(e));

            try {
                logger.error(
                        "Failed to derive value"
                                + name().map(name -> " '" + name + "'").orElse("")
                                + sourceValue.name().map(name -> " from '" + name + "'").orElse(""),
                        e
                );
            } catch (Exception ignore) {
                // failure here must not stop following code
            }
        } finally {
            this.valueVersion++;
        }

        Either<RuntimeException, T> valueToDispose = oldValue;
        try {
            disposeFunction.ifPresent(disposeFunction -> {
                valueToDispose.handleRight(disposeFunction::accept);
            });
        } catch (Exception loggableException) {
            logger.error("Failed to dispose old value", loggableException);
        }
    }
}
