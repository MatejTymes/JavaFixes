package javafixes.beta.change.builder;

import javafixes.beta.change.CachedValue;
import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.config.ScheduledReCachingConfig;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReCacheValueIf;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class CachedValueBuilder<T> implements ChangingValueBuilder<T> {

    private final ChangingValue<T> sourceValue;

    private Optional<String> valueName = Optional.empty();
    private ChangingValueUpdateConfig<? super T> updateConfig = ChangingValueUpdateConfig.DO_NOTHING_ON_UPDATE_CONFIG;
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

    @Override
    public CachedValue<T> build() {
        return new CachedValue<>(
                valueName,
                sourceValue,
                updateConfig,
                reCacheValueOnValueRetrievalIf,
                scheduledReCachingConfig,
                prePopulateValueImmediately
        );
    }

    public CachedValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public CachedValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfig = updateConfig.copyWithReplaceOldValueIf((Optional) Optional.of(replaceOldValueIf));
        return this;
    }

    public CachedValueBuilder<T> withForEachValueFunction(FailableValueHandler<? super T> forEachValueFunction) {
        this.updateConfig = updateConfig.copyWithForEachValueFunction((Optional) Optional.of(forEachValueFunction));
        return this;
    }

    public CachedValueBuilder<T> withAfterValueChangedFunction(Consumer<? super T> afterValueChangedFunction) {
        this.updateConfig = updateConfig.copyWithAfterValueChangedFunction((Optional) Optional.of(afterValueChangedFunction));
        return this;
    }

    public CachedValueBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = updateConfig.copyWithDisposeFunction((Optional) Optional.of(disposeFunction));
        return this;
    }

    public CachedValueBuilder<T> withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = updateConfig;
        return this;
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
}
