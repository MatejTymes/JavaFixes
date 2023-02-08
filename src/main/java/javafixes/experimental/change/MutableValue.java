package javafixes.experimental.change;

import javafixes.experimental.change.function.UseNewValueCheck;
import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.experimental.change.ChangingValueUtil.*;
import static javafixes.experimental.change.ChangingValueUtil.applyDisposeFunction;
import static javafixes.experimental.change.VersionedValue.initialValueVersion;

public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(MutableValue.class);


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
    public VersionedValue<T> getVersionedValue() {
        return currentValueHolder.get();
    }

    public boolean updateToNewValue(
            Either<RuntimeException, T> newValue,
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            VersionedValue<T> oldValue = currentValueHolder.get();

            boolean shouldUpdate = ignoreDifferenceCheck || shouldUpdate(
                    oldValue,
                    newValue,
                    useNewValueCheck,
                    valueName,
                    logger
            );

            if (shouldUpdate) {
                setNewValue(
                        oldValue,
                        newValue,
                        currentValueHolder
                );

                applyAfterValueChangedFunction(
                        newValue,
                        afterValueChangedFunction,
                        valueName,
                        logger
                );

                applyDisposeFunction(
                        oldValue,
                        disposeFunction,
                        valueName,
                        logger
                );
            }

            return shouldUpdate;
        }
    }
}
