package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.config.ScheduledReCachingConfig;
import javafixes.beta.change.function.AlwaysReCacheValue;
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

public class GenericCachedChangingValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(GenericCachedChangingValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;
    private final Optional<ReCacheValueCheck<T>> reCacheValueOnValueRetrievalCheck;
    private final Optional<ScheduledReCachingConfig<T>> scheduledReCachingConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastRetrievalOfSourceValueTimestamp = new AtomicReference<>();


    public GenericCachedChangingValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            Optional<ReCacheValueCheck<T>> reCacheValueOnValueRetrievalCheck,
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
            scheduledConfig.useExecutor.scheduleAtFixedRate(
                    () -> reCacheIfNeeded(
                            scheduledConfig.reCacheValueInBackgroundCheck.orElseGet(AlwaysReCacheValue::alwaysReCacheValue)
                    ),
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
            ReCacheValueCheck<T> reCacheCheck // nullable
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