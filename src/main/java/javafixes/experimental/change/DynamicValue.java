package javafixes.experimental.change;

import javafixes.experimental.change.function.UseNewValueCheck;
import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static javafixes.experimental.change.ChangingValueUtil.*;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

public class DynamicValue<T> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicValue.class);


    private final Optional<String> valueName;
    private final Supplier<T> valueGenerator;
    private final Optional<UseNewValueCheck> useNewValueCheck;
    private final Optional<Consumer<T>> afterValueChangedFunction;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<VersionedValue<T>> latestValueHolder = new AtomicReference<>();

    private DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            Optional<UseNewValueCheck> useNewValueCheck,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.useNewValueCheck = useNewValueCheck;
        this.afterValueChangedFunction = onValueChangedFunction;
        this.disposeFunction = disposeFunction;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        synchronized (latestValueHolder) {
            try {
                T generatedValue = valueGenerator.get();

                handleNewValue(right(generatedValue));
            } catch (RuntimeException e) {
                handleNewValue(left(e));
            }

            return latestValueHolder.get();
        }
    }

    private void handleNewValue(Either<RuntimeException, T> newValue) {
        VersionedValue<T> oldValue = latestValueHolder.get();

        boolean shouldUpdate = shouldUpdate(
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
                    latestValueHolder
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
    }

}
