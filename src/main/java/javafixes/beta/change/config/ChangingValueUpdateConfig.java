package javafixes.beta.change.config;

import javafixes.beta.change.function.UseNewValueCheck;

import java.util.Optional;
import java.util.function.Consumer;

public class ChangingValueUpdateConfig<T> {
    public final Optional<UseNewValueCheck<T>> useNewValueCheck;
    public final Optional<Consumer<T>> afterValueChangedFunction;
    public final Optional<Consumer<T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<UseNewValueCheck<T>> useNewValueCheck,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction
    ) {
        this.useNewValueCheck = useNewValueCheck;
        this.afterValueChangedFunction = afterValueChangedFunction;
        this.disposeFunction = disposeFunction;
    }
}
