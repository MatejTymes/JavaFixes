package javafixes.beta.change.config;

import javafixes.beta.change.function.ReCacheValueCheck;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledReCachingConfig<T> {
    public final ScheduledExecutorService useExecutor;
    public final Duration refreshPeriod;
    public final Optional<ReCacheValueCheck<? super T>> reCacheValueInBackgroundCheck;

    public ScheduledReCachingConfig(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod,
            Optional<ReCacheValueCheck<? super T>> reCacheValueInBackgroundCheck
    ) {
        this.useExecutor = useExecutor;
        this.refreshPeriod = refreshPeriod;
        this.reCacheValueInBackgroundCheck = reCacheValueInBackgroundCheck;
    }
}
