package javafixes.beta.change.config;

import javafixes.beta.change.function.AfterValueChangedHandler;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public final Optional<ReplaceOldValueIf<T>> replaceOldValueIf;

    public final Optional<FailableValueHandler<T>> onNewValueFunction;
    public final Optional<AfterValueChangedHandler<T>> afterValueChangedFunction;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ReplaceOldValueIf<T>> replaceOldValueIf, Optional<FailableValueHandler<T>> onNewValueFunction,
            Optional<AfterValueChangedHandler<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.onNewValueFunction = onNewValueFunction;
        this.replaceOldValueIf = replaceOldValueIf;
        this.afterValueChangedFunction = afterValueChangedFunction;
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
                this.onNewValueFunction,
                this.afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithReplaceOldValueIf(
            ReplaceOldValueIf<T> replaceOldValueIf
    ) {
        return copyWithReplaceOldValueIf(Optional.of(replaceOldValueIf));
    }

    public ChangingValueUpdateConfig<T> copyWithOnNewValueFunction(
            Optional<FailableValueHandler<T>> onNewValueFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                onNewValueFunction,
                this.afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithOnNewValueFunction(
            FailableValueHandler<T> onNewValueFunction
    ) {
        return copyWithOnNewValueFunction(Optional.of(onNewValueFunction));
    }

    public ChangingValueUpdateConfig<T> copyWithAfterValueChangedFunction(
            Optional<AfterValueChangedHandler<T>> afterValueChangedFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                this.onNewValueFunction,
                afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithAfterValueChangedFunction(
            AfterValueChangedHandler<T> afterValueChangedFunction
    ) {
        return copyWithAfterValueChangedFunction(Optional.of(afterValueChangedFunction));
    }

    public ChangingValueUpdateConfig<T> copyWithDisposeFunction(
            Optional<Consumer<T>> disposeFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                this.onNewValueFunction,
                this.afterValueChangedFunction,
                disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithDisposeFunction(
            Consumer<T> disposeFunction
    ) {
        return copyWithDisposeFunction(Optional.of(disposeFunction));
    }
}
