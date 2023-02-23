package javafixes.beta.change.config;

import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );


    public final Optional<ReplaceOldValueIf<T>> replaceOldValueIf;

    public final Optional<FailableValueHandler<T>> forEachValueFunction;
    public final Optional<Consumer<T>> afterValueChangedFunction; // todo: mtymes - change to Optional<BiConsumer<FailableValue<T>, FailableValue<T>>> // todo: mtymes - change to Optional<Consumer<FailableValue<T>>>
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<FailableValueHandler<T>> forEachValueFunction,
            Optional<ReplaceOldValueIf<T>> replaceOldValueIf,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.forEachValueFunction = forEachValueFunction;
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
                this.forEachValueFunction,
                replaceOldValueIf,
                this.afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithForEachValueFunction(
            Optional<FailableValueHandler<T>> forEachValueFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                forEachValueFunction,
                this.replaceOldValueIf,
                this.afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithAfterValueChangedFunction(
            Optional<Consumer<T>> afterValueChangedFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.forEachValueFunction,
                this.replaceOldValueIf,
                afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithDisposeFunction(
            Optional<Consumer<T>> disposeFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.forEachValueFunction,
                this.replaceOldValueIf,
                this.afterValueChangedFunction,
                disposeFunction
        );
    }
}
