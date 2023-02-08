package javafixes.experimental.change;

import javafixes.experimental.change.config.ChangingValueUpdateConfig;
import javafixes.object.Either;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.experimental.change.ChangingValueUtil.handleNewValue;
import static javafixes.experimental.change.VersionedValue.initialValueVersion;
import static javafixes.experimental.change.function.AlwaysUseNewValueCheck.alwaysUseNewValueCheck;
import static org.slf4j.LoggerFactory.getLogger;

public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(MutableValue.class);


    private final Optional<String> valueName;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();

    public MutableValue(
            Optional<String> valueName,
            Either<RuntimeException, T> initialValue,
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
            Either<RuntimeException, T> newValue,
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            return handleNewValue(
                    newValue,
                    currentValueHolder,
                    valueName,
                    ignoreDifferenceCheck ? Optional.of(alwaysUseNewValueCheck()) : updateConfig.useNewValueCheck,
                    updateConfig.afterValueChangedFunction,
                    updateConfig.disposeFunction,
                    logger
            );
        }
    }
}
