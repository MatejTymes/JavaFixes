package javafixes.object.changing.config;

import javafixes.object.changing.function.recaching.ReCacheValueIf;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class ScheduledReCachingConfig<T> {
    public final ScheduledExecutorService useExecutor;
    public final Optional<Duration> initialDelay;
    public final Duration refreshPeriod;
    public final Optional<ReCacheValueIf<? super T>> reCacheValueInBackgroundIf;

    public ScheduledReCachingConfig(
            ScheduledExecutorService useExecutor,
            Optional<Duration> initialDelay,
            Duration refreshPeriod,
            Optional<ReCacheValueIf<? super T>> reCacheValueInBackgroundIf
    ) {
        assertNotNull(useExecutor, "useExecutor", this.getClass());
        assertNotNull(initialDelay, "initialDelay", this.getClass());
        assertNotNull(refreshPeriod, "refreshPeriod", this.getClass());
        assertNotNull(reCacheValueInBackgroundIf, "reCacheValueInBackgroundIf", this.getClass());


        this.useExecutor = useExecutor;
        this.initialDelay = initialDelay;
        this.refreshPeriod = refreshPeriod;
        this.reCacheValueInBackgroundIf = reCacheValueInBackgroundIf;
    }

    public ScheduledReCachingConfig(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod,
            Optional<ReCacheValueIf<? super T>> reCacheValueInBackgroundIf
    ) {
        this(useExecutor, Optional.empty(), refreshPeriod, reCacheValueInBackgroundIf);
    }

    public ScheduledReCachingConfig(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod
    ) {
        this(useExecutor, Optional.empty(), refreshPeriod, Optional.empty());
    }
}
