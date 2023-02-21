package javafixes.beta.change.builder;

import javafixes.beta.change.*;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.config.ScheduledReCachingConfig;
import javafixes.beta.change.function.ReCacheValueCheck;
import javafixes.beta.change.function.ReplaceOldValueCheck;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class CachedChangingValueBuilder<T> implements ChangingValueBuilder<T> {

    private final ChangingValue<T> sourceValue;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueCheck<T>> shouldReplaceOldValueCheck = Optional.empty();
    private Optional<Consumer<T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<T>> disposeFunction = Optional.empty();
    private Optional<ReCacheValueCheck<T>> reCacheValueOnValueRetrievalCheck = Optional.empty();
    public Optional<ScheduledReCachingConfig<T>> scheduledReCachingConfig = Optional.empty();
    private boolean prePopulateValueImmediately = false;

    public CachedChangingValueBuilder(
            ChangingValue<T> sourceValue
    ) {
        this.sourceValue = sourceValue;
    }

    public static <T> CachedChangingValueBuilder<T> changingValueBuilder(
            ChangingValue<T> sourceValue
    ) {
        return new CachedChangingValueBuilder<>(sourceValue);
    }

    public static <T> CachedChangingValueBuilder<T> changingValueBuilder(
            ChangingValueBuilder<T> sourceValueBuilder
    ) {
        return new CachedChangingValueBuilder<>(sourceValueBuilder.build());
    }

    @Override
    public CachedChangingValue<T> build() {
        return new CachedChangingValue<>(
                valueName,
                sourceValue,
                new ChangingValueUpdateConfig<>(
                        shouldReplaceOldValueCheck,
                        afterValueChangedFunction,
                        disposeFunction
                ),
                reCacheValueOnValueRetrievalCheck,
                scheduledReCachingConfig,
                prePopulateValueImmediately
        );
    }

    public CachedChangingValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public CachedChangingValueBuilder<T> withReplaceOldValueCheck(ReplaceOldValueCheck<T> replaceOldValueCheck) {
        this.shouldReplaceOldValueCheck = Optional.of(replaceOldValueCheck);
        return this;
    }

    public CachedChangingValueBuilder<T> withAfterValueChangedFunction(Consumer<T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public CachedChangingValueBuilder<T> withDisposeFunction(Consumer<T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public CachedChangingValueBuilder<T> withReCacheValueOnValueRetrievalCheck(ReCacheValueCheck<T> reCacheValueOnValueRetrievalCheck) {
        this.reCacheValueOnValueRetrievalCheck = Optional.of(reCacheValueOnValueRetrievalCheck);
        return this;
    }

    public CachedChangingValueBuilder<T> withScheduledReCaching(
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

    public CachedChangingValueBuilder<T> withScheduledReCaching(
            ScheduledExecutorService useExecutor,
            Duration refreshPeriod,
            ReCacheValueCheck<T> reCacheValueInBackgroundCheck
    ) {
        this.scheduledReCachingConfig = Optional.of(new ScheduledReCachingConfig<>(
                useExecutor,
                refreshPeriod,
                Optional.of(reCacheValueInBackgroundCheck)
        ));
        return this;
    }

    public CachedChangingValueBuilder<T> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
