package javafixes.beta.change.config;

import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {
    public final Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf;
    public final Optional<Consumer<? super T>> afterValueChangedFunction;
    public final Optional<Consumer<? super T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf,
            Optional<Consumer<? super T>> afterValueChangedFunction,
            Optional<Consumer<? super T>> disposeFunction
    ) {
        this.replaceOldValueIf = replaceOldValueIf;
        this.afterValueChangedFunction = afterValueChangedFunction;
        this.disposeFunction = disposeFunction;
    }
}
