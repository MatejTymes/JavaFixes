package javafixes.beta.change.config;

import javafixes.beta.change.function.ReplaceOldValueCheck;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {
    public final Optional<ReplaceOldValueCheck<T>> shouldReplaceOldValueCheck;
    public final Optional<Consumer<T>> afterValueChangedFunction;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ReplaceOldValueCheck<T>> shouldReplaceOldValueCheck,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.shouldReplaceOldValueCheck = shouldReplaceOldValueCheck;
        this.afterValueChangedFunction = afterValueChangedFunction;
        this.disposeFunction = disposeFunction;
    }
}
