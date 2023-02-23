package javafixes.beta.change.config;

import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );


    public final Optional<ReplaceOldValueIf<T>> replaceOldValueIf;

    // todo: mtymes - add onValueSetFunction
    public final Optional<Consumer<T>> afterValueChangedFunction;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ReplaceOldValueIf<T>> replaceOldValueIf,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
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
                this.afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithAfterValueChangedFunction(
            Optional<Consumer<T>> afterValueChangedFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                afterValueChangedFunction,
                this.disposeFunction
        );
    }

    public ChangingValueUpdateConfig<T> copyWithDisposeFunction(
            Optional<Consumer<T>> disposeFunction
    ) {
        return new ChangingValueUpdateConfig<>(
                this.replaceOldValueIf,
                this.afterValueChangedFunction,
                disposeFunction
        );
    }
}
