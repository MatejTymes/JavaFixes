package javafixes.beta.change.builder;


import javafixes.beta.change.DynamicValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicValueBuilder<T> implements ChangingValueBuilder<T> {

    private final Supplier<T> valueGenerator;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueIf<T>> replaceOldValueIf = Optional.empty();
    private Optional<Consumer<T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<T>> disposeFunction = Optional.empty();

    public DynamicValueBuilder(
            Supplier<T> valueGenerator
    ) {
        this.valueGenerator = valueGenerator;
    }

    public static <T> DynamicValueBuilder<T> dynamicValueBuilder(Supplier<T> valueGenerator) {
        return new DynamicValueBuilder<>(valueGenerator);
    }

    @Override
    public DynamicValue<T> build() {
        return new DynamicValue<>(
                valueName,
                valueGenerator,
                new ChangingValueUpdateConfig<>(
                        replaceOldValueIf,
                        afterValueChangedFunction,
                        disposeFunction
                )
        );
    }

    public DynamicValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DynamicValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<T> replaceOldValueIf) {
        this.replaceOldValueIf = Optional.of(replaceOldValueIf);
        return this;
    }

    public DynamicValueBuilder<T> withAfterValueChangedFunction(Consumer<T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public DynamicValueBuilder<T> withDisposeFunction(Consumer<T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }
}
