package javafixes.experimental.change;

import javafixes.experimental.change.function.UseNewValueCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static javafixes.experimental.change.ChangingValueUtil.*;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;
import static org.slf4j.LoggerFactory.getLogger;

public class DynamicValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(DynamicValue.class);


    private final Optional<String> valueName;
    private final Supplier<T> valueGenerator;
    private final Optional<UseNewValueCheck> useNewValueCheck;
    private final Optional<Consumer<T>> afterValueChangedFunction;
    private final Optional<Consumer<T>> disposeFunction;

    private final AtomicReference<VersionedValue<T>> latestValueHolder = new AtomicReference<>();

    private DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            boolean prePopulateValueImmediately,
            Optional<UseNewValueCheck> useNewValueCheck,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.useNewValueCheck = useNewValueCheck;
        this.afterValueChangedFunction = onValueChangedFunction;
        this.disposeFunction = disposeFunction;

        if (prePopulateValueImmediately) {
            populateWithLatestValue();
        }
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        synchronized (latestValueHolder) {
            populateWithLatestValue();

            return latestValueHolder.get();
        }
    }

    private void populateWithLatestValue() {
        try {
            T generatedValue = valueGenerator.get();

            handleNewValue(
                    right(generatedValue),
                    latestValueHolder,
                    valueName,
                    useNewValueCheck,
                    afterValueChangedFunction,
                    disposeFunction,
                    logger
            );
        } catch (RuntimeException e) {
            handleNewValue(
                    left(e),
                    latestValueHolder,
                    valueName,
                    useNewValueCheck,
                    afterValueChangedFunction,
                    disposeFunction,
                    logger
            );
        }
    }
}
