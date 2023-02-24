package javafixes.beta.change.config;

import javafixes.beta.change.function.AfterValueChangedHandler;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public final Optional<ReplaceOldValueIf<T>> replaceOldValueIf;

    public final Optional<FailableValueHandler<T>> eachValueHandler;
    public final Optional<AfterValueChangedHandler<T>> afterValueChangedHandler;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ReplaceOldValueIf<T>> replaceOldValueIf, Optional<FailableValueHandler<T>> eachValueHandler,
            Optional<AfterValueChangedHandler<T>> afterValueChangedHandler,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.eachValueHandler = eachValueHandler;
        this.replaceOldValueIf = replaceOldValueIf;
        this.afterValueChangedHandler = afterValueChangedHandler;
        this.disposeFunction = disposeFunction;
    }

    public static <T> ChangingValueUpdateConfig<T> doNothingOnUpdateConfig() {
        return (ChangingValueUpdateConfig<T>) DO_NOTHING_ON_UPDATE_CONFIG;
    }

    public ChangingValueUpdateConfig<T> copyWithReplaceOldValueIf(
            Optional<ReplaceOldValueIf<T>> replaceOldValueIf
    ) {
        return new ChangingValueUpdateConfig<>(
                replaceOldValueIf,
                this.eachValueHandler,
                this.afterValueChangedHandler,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithReplaceOldValueIf(
            ReplaceOldValueIf<T> replaceOldValueIf
    ) {
        return copyWithReplaceOldValueIf(Optional.of(replaceOldValueIf));
    }

    public ChangingValueUpdateConfig<T> copyWithEachValueHandler(
            Optional<FailableValueHandler<T>> eachValueHandler
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                eachValueHandler,
                this.afterValueChangedHandler,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithEachValueHandler(
            FailableValueHandler<T> eachValueHandler
    ) {
        return copyWithEachValueHandler(Optional.of(eachValueHandler));
    }

    public ChangingValueUpdateConfig<T> copyWithAfterValueChangedHandler(
            Optional<AfterValueChangedHandler<T>> afterValueChangedHandler
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                this.eachValueHandler,
                afterValueChangedHandler,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithAfterValueChangedHandler(
            AfterValueChangedHandler<T> afterValueChangedHandler
    ) {
        return copyWithAfterValueChangedHandler(Optional.of(afterValueChangedHandler));
    }

    public ChangingValueUpdateConfig<T> copyWithDisposeFunction(
            Optional<Consumer<T>> disposeFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                this.eachValueHandler,
                this.afterValueChangedHandler,
                disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithDisposeFunction(
            Consumer<T> disposeFunction
    ) {
        return copyWithDisposeFunction(Optional.of(disposeFunction));
    }
}
