package javafixes.experimental.change.config;

import javafixes.experimental.change.function.UseNewValueCheck;

import java.util.Optional;
import java.util.function.Consumer;

// todo: mtymes - start using this
public class ChangingValueUpdateConfig<T> {
    public final Optional<UseNewValueCheck> useNewValueCheck;
    public final Optional<Consumer<T>> afterValueChangedFunction;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<UseNewValueCheck> useNewValueCheck,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.useNewValueCheck = useNewValueCheck;
        this.afterValueChangedFunction = afterValueChangedFunction;
        this.disposeFunction = disposeFunction;
    }
}
