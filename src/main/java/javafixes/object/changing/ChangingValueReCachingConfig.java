package javafixes.object.changing;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class ChangingValueReCachingConfig<T> {

    public final Duration refreshPeriod;
    public final Optional<ScheduledExecutorService> usingExecutor;
    public final Optional<String> valueName;
    public final Optional<Consumer<T>> onValueChangedFunction;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueReCachingConfig(
            Duration refreshPeriod,
            Optional<ScheduledExecutorService> usingExecutor,
            Optional<String> valueName,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.usingExecutor = usingExecutor;
        this.refreshPeriod = refreshPeriod;
        this.valueName = valueName;
        this.onValueChangedFunction = onValueChangedFunction;
        this.disposeFunction = disposeFunction;
    }

    public static <T> ChangingValueReCachingConfig<T> reCacheEach(
            Duration refreshPeriod
    ) {
        return new ChangingValueReCachingConfig<>(
                refreshPeriod,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public ChangingValueReCachingConfig<T> withRefreshPeriod(Duration refreshPeriod) {
        return new ChangingValueReCachingConfig<>(
                refreshPeriod,
                this.usingExecutor,
                this.valueName,
                this.onValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withValueRefreshInBackgroundProcess(ScheduledExecutorService usingExecutor) {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                Optional.of(usingExecutor),
                this.valueName,
                this.onValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withValueRefreshOnValueCall() {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                Optional.empty(),
                this.valueName,
                this.onValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withValueName(String valueName) {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                Optional.of(valueName),
                this.onValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withNoValueName() {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                Optional.empty(),
                this.onValueChangedFunction,
                this.disposeFunction
        );
    }


    public ChangingValueReCachingConfig<T> withValueName(Optional<String> valueName) {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                valueName,
                this.onValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withOnValueChangedFunction(Consumer<T> onValueChangedFunction) {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                this.valueName,
                Optional.of(onValueChangedFunction),
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withNoOnValueChangedFunction() {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                this.valueName,
                Optional.empty(),
                this.disposeFunction
        );
    }

    public ChangingValueReCachingConfig<T> withDisposeFunction(Consumer<T> disposeFunction) {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                this.valueName,
                this.onValueChangedFunction,
                Optional.of(disposeFunction)
        );
    }

    public ChangingValueReCachingConfig<T> withNoDisposeFunction() {
        return new ChangingValueReCachingConfig<>(
                this.refreshPeriod,
                this.usingExecutor,
                this.valueName,
                this.onValueChangedFunction,
                Optional.empty()
        );
    }
}
