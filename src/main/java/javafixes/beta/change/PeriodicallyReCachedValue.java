package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class PeriodicallyReCachedValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = getLogger(PeriodicallyReCachedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastCachingTimestamp = new AtomicReference<>();

    public PeriodicallyReCachedValue(
            Optional<String> valueName,
            ChangingValue<T> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            Duration refreshPeriod,
            ScheduledExecutorService usingExecutor
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.updateConfig = updateConfig;

        forceNewValueReCaching();

        usingExecutor.scheduleAtFixedRate(
                this::forceNewValueReCaching,
                refreshPeriod.toMillis(),
                refreshPeriod.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
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
