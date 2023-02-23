package javafixes.beta.change.config;

import javafixes.beta.change.function.ReCacheValueIf;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledReCachingConfig<T> {
    public final ScheduledExecutorService useExecutor;
    public final Duration refreshPeriod;
    public final Optional<ReCacheValueIf<T>> reCacheValueInBackgroundIf;

    public ScheduledReCachingConfig(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod,
            Optional<ReCacheValueIf<T>> reCacheValueInBackgroundIf
    ) {
        this.useExecutor = useExecutor;
        this.refreshPeriod = refreshPeriod;
        this.reCacheValueInBackgroundIf = reCacheValueInBackgroundIf;
    }
}
