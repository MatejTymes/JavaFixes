package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class ReCachedChangingValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = getLogger(ReCachedChangingValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;
    private final long refreshPeriodInMS;


    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastCachingTimestamp = new AtomicReference<>();


    public ReCachedChangingValue(
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
            // prevent multiple threads from updating the value on reaching cache eviction threshold at the same time
            synchronized (currentValueHolder) {
                if (System.currentTimeMillis() > lastCachingTimestamp.get() + refreshPeriodInMS) {
                    forceNewValueReCaching();
                }
            }
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
