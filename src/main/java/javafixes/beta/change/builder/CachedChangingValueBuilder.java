package javafixes.beta.change.builder;

import javafixes.beta.change.*;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ShouldReplaceOldValueCheck;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class CachedChangingValueBuilder<T> implements ChangingValueBuilder<T> {

    private final ChangingValue<T> sourceValue;

    private Optional<String> valueName = Optional.empty();
    private Optional<ShouldReplaceOldValueCheck<T>> shouldReplaceOldValueCheck = Optional.empty();
    private Optional<Consumer<T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<T>> disposeFunction = Optional.empty();
    private Optional<Duration> refreshPeriod = Optional.empty();
    private Optional<ScheduledExecutorService> usingExecutor = Optional.empty();
    private boolean prePopulateValueImmediately = false;

    public CachedChangingValueBuilder(
            ChangingValue<T> sourceValue
    ) {
        this.sourceValue = sourceValue;
    }

    public static <T> ChangingValueBuilder<T> changingValueBuilder(
            ChangingValue<T> sourceValue
    ) {
        return new CachedChangingValueBuilder<>(sourceValue);
    }

    public static <T> ChangingValueBuilder<T> changingValueBuilder(
            ChangingValueBuilder<T> sourceValueBuilder
    ) {
        return new CachedChangingValueBuilder<>(sourceValueBuilder.build());
    }

    @Override
    public CachedChangingValue<T> build() {
        if (usingExecutor.isPresent()) {
            if (!refreshPeriod.isPresent()) {
                throw new IllegalStateException("refreshPeriod must be defined");
            }
            return new AutoReCachedValue<>(
                    valueName,
                    sourceValue,
                    new ChangingValueUpdateConfig<>(
                            shouldReplaceOldValueCheck,
                            afterValueChangedFunction,
                            disposeFunction
                    ),
                    refreshPeriod.get(),
                    usingExecutor.get()
            );
        } else if (refreshPeriod.isPresent()) {
            return new ReCachedValue<>(
                    valueName,
                    sourceValue,
                    new ChangingValueUpdateConfig<>(
                            shouldReplaceOldValueCheck,
                            afterValueChangedFunction,
                            disposeFunction
                    ),
                    refreshPeriod.get(),
                    prePopulateValueImmediately
            );
        } else {
            return new SimpleCachedValue<>(
                    valueName,
                    sourceValue,
                    new ChangingValueUpdateConfig<>(
                            shouldReplaceOldValueCheck,
                            afterValueChangedFunction,
                            disposeFunction
                    ),
                    prePopulateValueImmediately
            );
        }
    }

    public CachedChangingValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public CachedChangingValueBuilder<T> withShouldReplaceOldValueCheck(ShouldReplaceOldValueCheck<T> shouldReplaceOldValueCheck) {
        this.shouldReplaceOldValueCheck = Optional.of(shouldReplaceOldValueCheck);
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

    public CachedChangingValueBuilder<T> withRefreshPeriod(Duration refreshPeriod) {
        this.refreshPeriod = Optional.of(refreshPeriod);
        return this;
    }

    public CachedChangingValueBuilder<T> withPeriodicBackgroundRefresh(ScheduledExecutorService usingExecutor, Duration refreshPeriod) {
        this.usingExecutor = Optional.of(usingExecutor);
        this.refreshPeriod = Optional.of(refreshPeriod);
        return this;
    }

    public CachedChangingValueBuilder<T> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
