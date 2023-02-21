package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static javafixes.beta.change.ChangingValueHelper.handleNewValue;
import static org.slf4j.LoggerFactory.getLogger;

public class AutoReCachedValue<T> implements CachedChangingValue<T> {

    private static final Logger logger = getLogger(AutoReCachedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<T> sourceValue;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastRetrievalOfSourceValueTimestamp = new AtomicReference<>();

    public AutoReCachedValue(
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
    public long getLastRetrievalOfSourceValueTimestamp() {
        return lastRetrievalOfSourceValueTimestamp.get();
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

            lastRetrievalOfSourceValueTimestamp.set(System.currentTimeMillis());
        }
    }
}