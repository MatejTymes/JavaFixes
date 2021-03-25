package javafixes.object.changing;

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
public class DerivedValue<T, SourceType> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);

    private final Optional<String> valueName;
    private final ChangingValue<SourceType> sourceValue;
    private final Function<SourceType, ? extends T> valueMapper;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long changeVersion;
    private long lastSourceChangeVersion;

    DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            Function<SourceType, ? extends T> valueMapper,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.disposeFunction = disposeFunction;

        deriveValue();
        this.changeVersion = 0;
    }

    private DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            Function<SourceType, ? extends T> valueMapper,
            Optional<Consumer<T>> disposeFunction,

            Either<RuntimeException, T> currentValue,
            long changeVersion,
            long lastSourceChangeVersion

    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.disposeFunction = disposeFunction;

        this.currentValue.set(currentValue);
        this.changeVersion = changeVersion;
        this.lastSourceChangeVersion = lastSourceChangeVersion;
    }

    public DerivedValue<T, SourceType> withValueName(String valueName) {
        synchronized (currentValue) {
            return new DerivedValue<>(Optional.of(valueName), sourceValue, valueMapper, disposeFunction, currentValue.get(), changeVersion, lastSourceChangeVersion);
        }
    }

    public DerivedValue<T, SourceType> withNoValueName() {
        synchronized (currentValue) {
            return new DerivedValue<>(Optional.empty(), sourceValue, valueMapper, disposeFunction, currentValue.get(), changeVersion, lastSourceChangeVersion);
        }
    }

    public DerivedValue<T, SourceType> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new DerivedValue<>(valueName, sourceValue, valueMapper, Optional.of(disposeFunction), currentValue.get(), changeVersion, lastSourceChangeVersion);
        }
    }

    public DerivedValue<T, SourceType> withNoDisposeFunction() {
        synchronized (currentValue) {
            return new DerivedValue<>(valueName, sourceValue, valueMapper, Optional.empty(), currentValue.get(), changeVersion, lastSourceChangeVersion);
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
    public long changeVersion() {
        ensureIsDerivedFromLatestVersion();

        return changeVersion;
    }

    private void ensureIsDerivedFromLatestVersion() {
        if (lastSourceChangeVersion != sourceValue.changeVersion()) {
            synchronized (currentValue) {
                if (lastSourceChangeVersion != sourceValue.changeVersion()) {
                    deriveValue();
                }
            }
        }
    }

    private void deriveValue() {
        this.lastSourceChangeVersion = sourceValue.changeVersion();

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
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
        } finally {
            this.changeVersion++;
        }

        Either<RuntimeException, T> valueToDispose = oldValue;
        try {
            disposeFunction.ifPresent(disposeFunction -> {
                valueToDispose.handleRight(disposeFunction::accept);
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
