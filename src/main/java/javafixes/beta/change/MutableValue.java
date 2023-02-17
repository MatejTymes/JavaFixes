package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static javafixes.beta.change.VersionedValue.initialValueVersion;
import static org.slf4j.LoggerFactory.getLogger;

public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(MutableValue.class);


    private final Optional<String> valueName;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();

    public MutableValue(
            Optional<String> valueName,
            FailableValue<T> initialValue,
            ChangingValueUpdateConfig<T> updateConfig
    ) {
        this.valueName = valueName;
        this.updateConfig = updateConfig;

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
            FailableValue<T> newValue,
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            return handleNewValue(
                    newValue,
                    currentValueHolder,
                    valueName,
                    ignoreDifferenceCheck,
                    updateConfig,
                    logger
            );
        }
    }
}
