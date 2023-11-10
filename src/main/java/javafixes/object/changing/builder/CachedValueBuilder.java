package javafixes.object.changing.builder;

import javafixes.object.changing.CachedValue;
import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.config.ScheduledReCachingConfig;
import javafixes.object.changing.function.recaching.ReCacheValueIf;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public class CachedValueBuilder<T> extends AbstractChangingValueBuilder<T, CachedValueBuilder<T>> {

    private final ChangingValue<T> sourceValue;

    private Optional<ReCacheValueIf<? super T>> reCacheValueOnValueRetrievalIf = Optional.empty();
    public Optional<ScheduledReCachingConfig<? super T>> scheduledReCachingConfig = Optional.empty();
    private boolean prePopulateValueImmediately = false;

    public CachedValueBuilder(
            ChangingValue<T> sourceValue
    ) {
        this.sourceValue = sourceValue;
    }


    public static <T> CachedValueBuilder<T> cachedValueBuilder(
            ChangingValue<T> sourceValue
    ) {
        return new CachedValueBuilder<>(sourceValue);
    }

    public static <T> CachedValueBuilder<T> cachedValueBuilder(
            ChangingValueBuilder<T> sourceValueBuilder
    ) {
        return new CachedValueBuilder<>(sourceValueBuilder.build());
    }


    public static <T> CachedValue<T> cachedValue(
            ChangingValue<T> sourceValue
    ) {
        return cachedValueBuilder(sourceValue).build();
    }

    public static <T> CachedValue<T> cachedValue(
            ChangingValueBuilder<T> sourceValueBuilder
    ) {
        return cachedValueBuilder(sourceValueBuilder).build();
    }


    @Override
    public CachedValue<T> build() {
        return new CachedValue<>(
                valueName,
                sourceValue,
                updateConfig(),
                reCacheValueOnValueRetrievalIf,
                scheduledReCachingConfig,
                prePopulateValueImmediately
        );
    }

    public CachedValueBuilder<T> withReCacheValueOnValueRetrievalIf(ReCacheValueIf<? super T> reCacheValueOnValueRetrievalCheck) {
        this.reCacheValueOnValueRetrievalIf = Optional.of(reCacheValueOnValueRetrievalCheck);
        return this;
    }

    public CachedValueBuilder<T> withScheduledReCaching(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod
    ) {
        this.scheduledReCachingConfig = Optional.of(new ScheduledReCachingConfig<>(
                useExecutor,
                refreshPeriod,
                Optional.empty()
        ));
        return this;
    }

    public CachedValueBuilder<T> withScheduledReCaching(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod,
            ReCacheValueIf<? super T> reCacheValueInBackgroundIf
    ) {
        this.scheduledReCachingConfig = Optional.of(new ScheduledReCachingConfig<>(
                useExecutor,
                refreshPeriod,
                Optional.of(reCacheValueInBackgroundIf)
        ));
        return this;
    }

    public CachedValueBuilder<T> withScheduledReCaching(
            ScheduledReCachingConfig<? super T> reCacheConfig
    ) {
        this.scheduledReCachingConfig = Optional.of(reCacheConfig);
        return this;
    }

    public CachedValueBuilder<T> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }

    @Override
    protected CachedValueBuilder<T> thisBuilder() {
        return this;
    }
}
