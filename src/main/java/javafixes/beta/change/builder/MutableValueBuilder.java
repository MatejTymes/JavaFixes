package javafixes.beta.change.builder;

import javafixes.beta.change.FailableValue;
import javafixes.beta.change.MutableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;

public class MutableValueBuilder<T> implements ChangingValueBuilder<T> {

    private final FailableValue<T> initialValue;

    private Optional<String> valueName = Optional.empty();
    private ChangingValueUpdateConfig<? super T> updateConfig = ChangingValueUpdateConfig.DO_NOTHING_ON_UPDATE_CONFIG;

    public MutableValueBuilder(
            FailableValue<T> initialValue
    ) {
        this.initialValue = initialValue;
    }

    public static <T> MutableValueBuilder<T> mutableValueBuilder(FailableValue<T> value) {
        return new MutableValueBuilder<>(value);
    }

    public static <T> MutableValueBuilder<T> mutableValueBuilder(T value) {
        return new MutableValueBuilder<>(wrapValue(value));
    }

    public static <T> MutableValueBuilder<T> mutableValueBuilderWithFailure(RuntimeException failure) {
        return new MutableValueBuilder<>(wrapFailure(failure));
    }

    @Override
    public MutableValue<T> build() {
        return new MutableValue<>(
                valueName,
                initialValue,
                updateConfig
        );
    }

    public MutableValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public MutableValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfig = updateConfig.copyWithReplaceOldValueIf((Optional) Optional.of(replaceOldValueIf));
        return this;
    }

    public MutableValueBuilder<T> withAfterValueChangedFunction(Consumer<? super T> afterValueChangedFunction) {
        this.updateConfig = updateConfig.copyWithAfterValueChangedFunction((Optional) Optional.of(afterValueChangedFunction));
        return this;
    }

    public MutableValueBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = updateConfig.copyWithDisposeFunction((Optional) Optional.of(disposeFunction));
        return this;
    }

    public MutableValueBuilder<T> withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = updateConfig;
        return this;
    }
}
