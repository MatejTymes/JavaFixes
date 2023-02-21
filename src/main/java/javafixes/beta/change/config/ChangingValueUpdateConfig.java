package javafixes.beta.change.config;

import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {
    public final Optional<ReplaceOldValueIf<T>> replaceOldValueIf;
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
}
