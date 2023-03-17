package javafixes.object.changing.config;

import javafixes.object.changing.function.ReCacheValueIf;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledReCachingConfig<T> {
    public final ScheduledExecutorService useExecutor;
    public final Duration refreshPeriod;
    public final Optional<ReCacheValueIf<? super T>> reCacheValueInBackgroundIf;

    public ScheduledReCachingConfig(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod,
            Optional<ReCacheValueIf<? super T>> reCacheValueInBackgroundIf
    ) {
        this.useExecutor = useExecutor;
        this.refreshPeriod = refreshPeriod;
        this.reCacheValueInBackgroundIf = reCacheValueInBackgroundIf;
    }
}
