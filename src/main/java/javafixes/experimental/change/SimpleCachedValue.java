package javafixes.experimental.change;

import javafixes.experimental.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.experimental.change.ChangingValueUtil.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class SimpleCachedValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = getLogger(SimpleCachedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;
    private final long refreshPeriodInMS;


    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastCachingTimestamp = new AtomicReference<>();


    public SimpleCachedValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            Duration refreshPeriod
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.updateConfig = updateConfig;
        this.refreshPeriodInMS = refreshPeriod.toMillis();

        forceNewValueReCaching();
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        if (System.currentTimeMillis() > lastCachingTimestamp.get() + refreshPeriodInMS) {
            forceNewValueReCaching();
        }

        return currentValueHolder.get();
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
