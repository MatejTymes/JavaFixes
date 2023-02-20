package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedValue;
import javafixes.beta.change.FailableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ShouldReplaceOldValueCheck;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedValueBuilder<SourceType, OutputType> implements ChangingValueBuilder<OutputType> {

    private final ChangingValue<SourceType> sourceValue;
    private final Function<FailableValue<SourceType>, ? extends OutputType> valueMapper;

    private Optional<String> valueName = Optional.empty();
    private Optional<ShouldReplaceOldValueCheck<OutputType>> shouldReplaceOldValueCheck = Optional.empty();
    private Optional<Consumer<OutputType>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<OutputType>> disposeFunction = Optional.empty();
    private boolean prePopulateValueImmediately = false;

    public DerivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            Function<FailableValue<SourceType>, ? extends OutputType> valueMapper
    ) {
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
    }

    public static <SourceType, OutputType> DerivedValueBuilder<SourceType, OutputType> derivedValueBuilder(
            ChangingValue<SourceType> sourceType,
            Function<FailableValue<SourceType>, ? extends OutputType> valueMapper
    ) {
        return new DerivedValueBuilder<>(sourceType, valueMapper);
    }

    public static <SourceType, OutputType> Function<FailableValue<SourceType>, ? extends OutputType> mappingValue(
            Function<SourceType, ? extends  OutputType> valueMapper
    ) {
        return failableValue -> valueMapper.apply(failableValue.value());
    }

    @Override
    public DerivedValue<SourceType, OutputType> build() {
        return new DerivedValue<>(
                valueName,
                sourceValue,
                new ChangingValueUpdateConfig<>(
                        shouldReplaceOldValueCheck,
                        afterValueChangedFunction,
                        disposeFunction
                ),
                valueMapper,
                prePopulateValueImmediately
        );
    }

    public DerivedValueBuilder<SourceType, OutputType> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withShouldReplaceOldValueCheck(ShouldReplaceOldValueCheck<OutputType> shouldReplaceOldValueCheck) {
        this.shouldReplaceOldValueCheck = Optional.of(shouldReplaceOldValueCheck);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withAfterValueChangedFunction(Consumer<OutputType> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withDisposeFunction(Consumer<OutputType> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
