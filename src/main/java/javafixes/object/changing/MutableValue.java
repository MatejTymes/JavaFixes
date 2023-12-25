package javafixes.object.changing;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.object.changing.ChangingValueHelper.handlePotentialNewValue;
import static javafixes.common.util.AssertUtil.assertNotNull;
import static javafixes.object.changing.FailableValue.wrapValue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link MutableValue} is a wrapper of value that you can replace and whose changes will be propagated
 * to derived values.
 *
 * @param <T> type of wrapped value
 * @author mtymes
 */
public class MutableValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(MutableValue.class);


    private final Optional<String> valueName;
    private final ChangingValueUpdateConfig<? super T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();

    public MutableValue(
            Optional<String> valueName,
            FailableValue<T> initialValue,
            ChangingValueUpdateConfig<? super T> updateConfig
    ) {
        assertNotNull(valueName, "valueName", this.getClass());
        assertNotNull(initialValue, "initialValue", this.getClass());
        assertNotNull(updateConfig, "updateConfig", this.getClass());

        this.valueName = valueName;
        this.updateConfig = updateConfig;

        handlePotentialNewValue(
                initialValue,
                currentValueHolder,
                valueName,
                true,
                updateConfig,
                logger
        );
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> versionedValue() {
        return currentValueHolder.get();
    }

    public boolean updateValue(
            FailableValue<T> newValue,
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            return handlePotentialNewValue(
                    newValue,
                    currentValueHolder,
                    valueName,
                    ignoreDifferenceCheck,
                    updateConfig,
                    logger
            );
        }
    }

    public boolean updateValue(
            FailableValue<T> newValue
    ) {
        return updateValue(newValue, false);
    }

    public boolean updateValue(
            T newValue,
            boolean ignoreDifferenceCheck
    ) {
        return updateValue(wrapValue(newValue), ignoreDifferenceCheck);
    }

    public boolean updateValue(
            T newValue
    ) {
        return updateValue(wrapValue(newValue), false);
    }
}
