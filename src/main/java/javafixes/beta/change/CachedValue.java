package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.config.ScheduledReCachingConfig;
import javafixes.beta.change.function.ReCacheValueIf;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueHelper.handlePotentialNewValue;
import static javafixes.common.Asserts.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public class CachedValue<T> implements ChangingValue<T> {

    private static final Logger logger = getLogger(CachedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<? super T> updateConfig;
    private final Optional<ReCacheValueIf<? super T>> reCacheValueOnValueRetrievalIf;
    private final Optional<ScheduledReCachingConfig<? super T>> scheduledReCachingConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastRetrievalOfSourceValueTimestamp = new AtomicReference<>();


    public CachedValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<? super T> updateConfig,
            Optional<ReCacheValueIf<? super T>> reCacheValueOnValueRetrievalIf,
            Optional<ScheduledReCachingConfig<? super T>> scheduledReCachingConfig,
            boolean prePopulateValueImmediately
    ) {
        assertNotNull(valueName, "valueName", "CachedValue");
        assertNotNull(sourceValue, "sourceValue", "CachedValue");
        assertNotNull(updateConfig, "updateConfig", "CachedValue");
        assertNotNull(reCacheValueOnValueRetrievalIf, "reCacheValueOnValueRetrievalIf", "CachedValue");
        assertNotNull(scheduledReCachingConfig, "scheduledReCachingConfig", "CachedValue");

        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.updateConfig = updateConfig;
        this.reCacheValueOnValueRetrievalIf = reCacheValueOnValueRetrievalIf;
        this.scheduledReCachingConfig = scheduledReCachingConfig;

        if (prePopulateValueImmediately) {
            forceReCaching(true);
        }

        if (scheduledReCachingConfig.isPresent()) {
            ScheduledReCachingConfig<? super T> scheduledConfig = scheduledReCachingConfig.get();
            ReCacheValueIf<? super T> backgroundReCacheCheck = scheduledConfig.reCacheValueInBackgroundIf.orElseGet(ReCacheValueIf::alwaysReCacheValue);
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
    public VersionedValue<T> versionedValue() {
        reCacheIfNeeded(reCacheValueOnValueRetrievalIf.orElse(null));

        return currentValueHolder.get();
    }

    public long getLastRetrievalOfSourceValueTimestamp() {
        return lastRetrievalOfSourceValueTimestamp.get();
    }


    public ZonedDateTime getLastRetrievalOfSourceValueTime(ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getLastRetrievalOfSourceValueTimestamp()), zoneId);
    }

    public void forceReCaching(
            boolean ignoreDifferenceCheck
    ) {
        synchronized (currentValueHolder) {
            VersionedValue<T> newValue = sourceValue.versionedValue();

            handlePotentialNewValue(
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

    public void forceReCaching() {
        forceReCaching(false);
    }

    private void reCacheIfNeeded(
            ReCacheValueIf<? super T> reCacheCheck // nullable
    ) {
        VersionedValue<T> value = currentValueHolder.get();

        boolean didInitializeValue = false;
        if (value == null) {
            synchronized (currentValueHolder) {
                // if multiple threads reach this point at the same time, only the first one should force and update
                if (currentValueHolder.get() == null) {
                    forceReCaching(true);
                    didInitializeValue = true;
                }
            }
        }

        if (!didInitializeValue && reCacheCheck != null) {
            synchronized (currentValueHolder) {
                if (reCacheCheck.reCacheValueIf(value.failableValue(), lastRetrievalOfSourceValueTimestamp.get())) {
                    forceReCaching(false);
                }
            }
        }
    }
}
