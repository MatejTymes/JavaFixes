package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;
import static org.slf4j.LoggerFactory.getLogger;

public class DynamicValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(DynamicValue.class);


    private final Optional<String> valueName;
    private final Supplier<T> valueGenerator;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> latestValueHolder = new AtomicReference<>();

    private DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            boolean prePopulateValueImmediately,
            ChangingValueUpdateConfig<T> updateConfig
    ) {
        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.updateConfig = updateConfig;

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
                    updateConfig,
                    logger
            );
        } catch (RuntimeException e) {
            handleNewValue(
                    left(e),
                    latestValueHolder,
                    valueName,
                    updateConfig,
                    logger
            );
        }
    }
}
