package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class ReCachedValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = getLogger(ReCachedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;
    private final long refreshPeriodInMS;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastCachingTimestamp = new AtomicReference<>();


    public ReCachedValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            Duration refreshPeriod,
            boolean prePopulateValueImmediately
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.updateConfig = updateConfig;
        this.refreshPeriodInMS = refreshPeriod.toMillis();

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
        if (currentValueHolder.get() == null) {
            synchronized (currentValueHolder) {
                // if multiple threads reach this point at the same time, only the first one should force and update
                if (currentValueHolder.get() == null) {
                    forceNewValueReCaching();
                }
            }
        } else if (System.currentTimeMillis() > lastCachingTimestamp.get() + refreshPeriodInMS) {
            // if multiple threads reach this point at the same time, only the first one should force and update
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
