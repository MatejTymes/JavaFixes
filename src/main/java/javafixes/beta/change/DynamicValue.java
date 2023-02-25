package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static javafixes.beta.change.ChangingValueHelper.handlePotentialNewValue;
import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;
import static javafixes.common.Asserts.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public class DynamicValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(DynamicValue.class);


    private final Optional<String> valueName;
    private final Supplier<T> valueGenerator;
    private final ChangingValueUpdateConfig<? super T> updateConfig;

    private final AtomicReference<VersionedValue<T>> latestValueHolder = new AtomicReference<>();

    public DynamicValue(
            Optional<String> valueName,
            Supplier<T> valueGenerator,
            ChangingValueUpdateConfig<? super T> updateConfig
    ) {
        assertNotNull(valueName, "valueName", "DynamicValue");
        assertNotNull(valueGenerator, "valueGenerator", "DynamicValue");
        assertNotNull(updateConfig, "updateConfig", "DynamicValue");

        this.valueName = valueName;
        this.valueGenerator = valueGenerator;
        this.updateConfig = updateConfig;
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

            handlePotentialNewValue(
                    wrapValue(generatedValue),
                    latestValueHolder,
                    valueName,
                    updateConfig,
                    logger
            );
        } catch (RuntimeException e) {
            handlePotentialNewValue(
                    wrapFailure(e),
                    latestValueHolder,
                    valueName,
                    updateConfig,
                    logger
            );
        }
    }
}
