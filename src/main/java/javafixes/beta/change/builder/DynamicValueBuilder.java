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
    private ChangingValueUpdateConfig<? super T> updateConfig = ChangingValueUpdateConfig.DO_NOTHING_ON_UPDATE_CONFIG;

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
                updateConfig
        );
    }

    public DynamicValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DynamicValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfig = updateConfig.copyWithReplaceOldValueIf((Optional) Optional.of(replaceOldValueIf));
        return this;
    }

    public DynamicValueBuilder<T> withAfterValueChangedFunction(Consumer<? super T> afterValueChangedFunction) {
        this.updateConfig = updateConfig.copyWithAfterValueChangedFunction((Optional) Optional.of(afterValueChangedFunction));
        return this;
    }

    public DynamicValueBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = updateConfig.copyWithDisposeFunction((Optional) Optional.of(disposeFunction));
        return this;
    }

    public DynamicValueBuilder<T> withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = updateConfig;
        return this;
    }
}
