package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class SimpleCachedValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = getLogger(SimpleCachedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;


    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastCachingTimestamp = new AtomicReference<>();


    public SimpleCachedValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            boolean prePopulateValueImmediately
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.updateConfig = updateConfig;

        if (prePopulateValueImmediately) {
            forceNewValueReCaching();
        }
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        VersionedValue<T> value = currentValueHolder.get();
        if (value == null) {
            synchronized (currentValueHolder) {
                // if multiple threads reach this point at the same time, only the first one should force and update
                value = currentValueHolder.get();
                if (value == null) {
                    forceNewValueReCaching();
                    value = currentValueHolder.get();
                }
            }
        }
        return value;
    }

    @Override
    public long getLastCachingTimestamp() {
        return lastCachingTimestamp.get();
    }

    @Override
    public void forceNewValueReCaching(
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            VersionedValue<T> newValue = sourceValue.getVersionedValue();

            handleNewValue(
                    newValue.value,
                    currentValueHolder,
                    valueName,
                    ignoreDifferenceCheck,
                    updateConfig,
                    logger
            );

            lastCachingTimestamp.set(System.currentTimeMillis());
        }
    }
}
