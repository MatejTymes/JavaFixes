package javafixes.experimental.change;

import javafixes.experimental.change.function.UseNewValueCheck;
import javafixes.object.Either;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.experimental.change.ChangingValueUtil.handleNewValue;
import static javafixes.experimental.change.VersionedValue.initialValueVersion;
import static javafixes.experimental.change.function.AlwaysUseNewValueCheck.alwaysUseNewValueCheck;
import static org.slf4j.LoggerFactory.getLogger;

public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(MutableValue.class);


    private final Optional<String> valueName;
    private final Optional<UseNewValueCheck> useNewValueCheck;
    private final Optional<Consumer<T>> afterValueChangedFunction;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();

    public MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> initialValue,
            Optional<UseNewValueCheck> useNewValueCheck,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.useNewValueCheck = useNewValueCheck;
        this.afterValueChangedFunction = afterValueChangedFunction;
        this.disposeFunction = disposeFunction;

        this.currentValueHolder.set(initialValueVersion(initialValue));
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        return currentValueHolder.get();
    }

    public boolean updateToNewValue(
            Either<RuntimeException, T> newValue,
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            return handleNewValue(
                    newValue,
                    currentValueHolder,
                    valueName,
                    ignoreDifferenceCheck ? Optional.of(alwaysUseNewValueCheck()) : useNewValueCheck,
                    afterValueChangedFunction,
                    disposeFunction,
                    logger
            );
        }
    }
}
