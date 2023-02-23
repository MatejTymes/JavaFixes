package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedValue;
import javafixes.beta.change.FailableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedValueBuilder<SourceType, OutputType> implements ChangingValueBuilder<OutputType> {

    private final ChangingValue<SourceType> sourceValue;
    private final Function<FailableValue<SourceType>, ? extends OutputType> valueMapper;

    private Optional<String> valueName = Optional.empty();
    private ChangingValueUpdateConfig<? super OutputType> updateConfig = ChangingValueUpdateConfig.DO_NOTHING_ON_UPDATE_CONFIG;
    private boolean prePopulateValueImmediately = false;

    public DerivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            Function<FailableValue<SourceType>, ? extends OutputType> valueMapper
    ) {
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
    }

    public static <SourceType, OutputType> DerivedValueBuilder<SourceType, OutputType> derivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            Function<FailableValue<SourceType>, ? extends OutputType> valueMapper
    ) {
        return new DerivedValueBuilder<>(sourceValue, valueMapper);
    }

    public static <SourceType, OutputType> DerivedValueBuilder<SourceType, OutputType> derivedValueBuilder(
            ChangingValueBuilder<SourceType> sourceValueBuilder,
            Function<FailableValue<SourceType>, ? extends OutputType> valueMapper
    ) {
        return derivedValueBuilder(sourceValueBuilder.build(), valueMapper);
    }

    @Override
    public DerivedValue<SourceType, OutputType> build() {
        return new DerivedValue<>(
                valueName,
                sourceValue,
                updateConfig,
                valueMapper,
                prePopulateValueImmediately
        );
    }

    public DerivedValueBuilder<SourceType, OutputType> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withReplaceOldValueIf(ReplaceOldValueIf<? super OutputType> replaceOldValueIf) {
        this.updateConfig = updateConfig.copyWithReplaceOldValueIf((Optional) Optional.of(replaceOldValueIf));
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withForEachValueFunction(FailableValueHandler<? super OutputType> forEachValueFunction) {
        this.updateConfig = updateConfig.copyWithForEachValueFunction((Optional) Optional.of(forEachValueFunction));
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withAfterValueChangedFunction(Consumer<? super OutputType> afterValueChangedFunction) {
        this.updateConfig = updateConfig.copyWithAfterValueChangedFunction((Optional) Optional.of(afterValueChangedFunction));
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withDisposeFunction(Consumer<? super OutputType> disposeFunction) {
        this.updateConfig = updateConfig.copyWithDisposeFunction((Optional) Optional.of(disposeFunction));
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withUpdateConfig(ChangingValueUpdateConfig<? super OutputType> updateConfig) {
        this.updateConfig = updateConfig;
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
