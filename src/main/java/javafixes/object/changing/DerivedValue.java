package javafixes.object.changing;

import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final boolean doUpdateIfNewAndOldValueAreEqual;

    private final AtomicReference<Either<RuntimeException, T>> currentValue = new AtomicReference<>();
    private long changeVersion;
    private long lastSourceChangeVersion;

    DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            Function<SourceType, ? extends T> valueMapper,
            Optional<Consumer<T>> disposeFunction,
            boolean doUpdateIfNewAndOldValueAreEqual
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.disposeFunction = disposeFunction;
        this.doUpdateIfNewAndOldValueAreEqual = doUpdateIfNewAndOldValueAreEqual;

        deriveValue();
        this.changeVersion = 0;
    }

    private DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            Function<SourceType, ? extends T> valueMapper,
            Optional<Consumer<T>> disposeFunction,
            boolean doUpdateIfNewAndOldValueAreEqual,

            Either<RuntimeException, T> currentValue,
            long changeVersion,
            long lastSourceChangeVersion

    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.disposeFunction = disposeFunction;
        this.doUpdateIfNewAndOldValueAreEqual = doUpdateIfNewAndOldValueAreEqual;

        this.currentValue.set(currentValue);
        this.changeVersion = changeVersion;
        this.lastSourceChangeVersion = lastSourceChangeVersion;
    }

    public DerivedValue<T, SourceType> withValueName(String valueName) {
        synchronized (currentValue) {
            return new DerivedValue<>(
                    Optional.of(valueName),
                    sourceValue,
                    valueMapper,
                    disposeFunction,
                    doUpdateIfNewAndOldValueAreEqual,
                    currentValue.get(),
                    changeVersion,
                    lastSourceChangeVersion
            );
        }
    }

    public DerivedValue<T, SourceType> withNoValueName() {
        synchronized (currentValue) {
            return new DerivedValue<>(
                    Optional.empty(),
                    sourceValue,
                    valueMapper,
                    disposeFunction,
                    doUpdateIfNewAndOldValueAreEqual,
                    currentValue.get(),
                    changeVersion,
                    lastSourceChangeVersion
            );
        }
    }

    public DerivedValue<T, SourceType> withValueName(Optional<String> optionalValueName) {
        if (optionalValueName.isPresent()) {
            return withValueName(optionalValueName.get());
        } else {
            return withNoValueName();
        }
    }

    public DerivedValue<T, SourceType> withDisposeFunction(Consumer<T> disposeFunction) {
        synchronized (currentValue) {
            return new DerivedValue<>(
                    valueName,
                    sourceValue,
                    valueMapper,
                    Optional.of(disposeFunction),
                    doUpdateIfNewAndOldValueAreEqual,
                    currentValue.get(),
                    changeVersion,
                    lastSourceChangeVersion
            );
        }
    }

    public DerivedValue<T, SourceType> withNoDisposeFunction() {
        synchronized (currentValue) {
            return new DerivedValue<>(
                    valueName,
                    sourceValue,
                    valueMapper,
                    Optional.empty(),
                    doUpdateIfNewAndOldValueAreEqual,
                    currentValue.get(),
                    changeVersion,
                    lastSourceChangeVersion
            );
        }
    }

    public DerivedValue<T, SourceType> withDisposeFunction(Optional<Consumer<T>> optionalDisposeFunction) {
        if (optionalDisposeFunction.isPresent()) {
            return withDisposeFunction(optionalDisposeFunction.get());
        } else {
            return withNoDisposeFunction();
        }
    }

    public DerivedValue<T, SourceType> withDoUpdateIfNewAndOldValueAreEqual(boolean doUpdateIfNewAndOldValueAreEqual) {
        synchronized (currentValue) {
            return new DerivedValue<>(
                    valueName,
                    sourceValue,
                    valueMapper,
                    disposeFunction,
                    doUpdateIfNewAndOldValueAreEqual,
                    currentValue.get(),
                    changeVersion,
                    lastSourceChangeVersion
            );
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

    public boolean isDoUpdateIfNewAndOldValueAreEqual() {
        return doUpdateIfNewAndOldValueAreEqual;
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

        AtomicBoolean didUpdateHappen = new AtomicBoolean(true);
        Either<RuntimeException, T> oldValue;
        try {
            T newValue = valueMapper.apply(sourceValue.value());
            if (doUpdateIfNewAndOldValueAreEqual) {
                oldValue = this.currentValue.getAndSet(right(newValue));
            } else {
                oldValue = this.currentValue.getAndAccumulate(
                        right(newValue),
                        (oldEither, newEither) -> {
                            if (oldEither.equals(newEither)) {
                                didUpdateHappen.set(false);
                                return oldEither;
                            } else {
                                return newEither;
                            }
                        }
                );
            }
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
            if (didUpdateHappen.get()) {
                this.changeVersion++;
            }
        }

        if (didUpdateHappen.get()) {
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
}
