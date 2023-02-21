package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.config.ScheduledReCachingConfig;
import javafixes.beta.change.function.ReCacheValueCheck;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueHelper.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class CachedChangingValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(CachedChangingValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;
    private final Optional<ReCacheValueCheck<? super T>> reCacheValueOnValueRetrievalCheck;
    private final Optional<ScheduledReCachingConfig<T>> scheduledReCachingConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastRetrievalOfSourceValueTimestamp = new AtomicReference<>();


    public CachedChangingValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            Optional<ReCacheValueCheck<? super T>> reCacheValueOnValueRetrievalCheck,
            Optional<ScheduledReCachingConfig<T>> scheduledReCachingConfig,
            boolean prePopulateValueImmediately
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.updateConfig = updateConfig;
        this.reCacheValueOnValueRetrievalCheck = reCacheValueOnValueRetrievalCheck;
        this.scheduledReCachingConfig = scheduledReCachingConfig;

        if (prePopulateValueImmediately) {
            forceNewValueReCaching();
        }

        if (scheduledReCachingConfig.isPresent()) {
            ScheduledReCachingConfig<T> scheduledConfig = scheduledReCachingConfig.get();
            ReCacheValueCheck<? super T> backgroundReCacheCheck = scheduledConfig.reCacheValueInBackgroundCheck.orElseGet(ReCacheValueCheck::alwaysReCacheValue);
            scheduledConfig.useExecutor.scheduleAtFixedRate(
                    () -> reCacheIfNeeded(backgroundReCacheCheck),
                    scheduledConfig.refreshPeriod.toMillis(),
                    scheduledConfig.refreshPeriod.toMillis(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        reCacheIfNeeded(reCacheValueOnValueRetrievalCheck.orElse(null));

        return currentValueHolder.get();
    }

    public long getLastRetrievalOfSourceValueTimestamp() {
        return lastRetrievalOfSourceValueTimestamp.get();
    }


    public ZonedDateTime getLastRetrievalOfSourceValueTime(ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getLastRetrievalOfSourceValueTimestamp()), zoneId);
    }

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

            lastRetrievalOfSourceValueTimestamp.set(System.currentTimeMillis());
        }
    }

    public void forceNewValueReCaching() {
        forceNewValueReCaching(false);
    }

    private void reCacheIfNeeded(
            ReCacheValueCheck<? super T> reCacheCheck // nullable
    ) {
        VersionedValue<T> value = currentValueHolder.get();

        boolean didInitializeValue = false;
        if (value == null) {
            synchronized (currentValueHolder) {
                // if multiple threads reach this point at the same time, only the first one should force and update
                if (currentValueHolder.get() == null) {
                    forceNewValueReCaching(true);
                    didInitializeValue = true;
                }
            }
        }

        if (!didInitializeValue && reCacheCheck != null) {
            synchronized (currentValueHolder) {
                if (reCacheCheck.reCacheValue(value.failableValue(), lastRetrievalOfSourceValueTimestamp.get())) {
                    forceNewValueReCaching(false);
                }
            }
        }
    }
}
