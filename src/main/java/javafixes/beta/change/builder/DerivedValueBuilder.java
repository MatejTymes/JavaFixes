package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedValue;
import javafixes.beta.change.FailableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedValueBuilder<SourceType, OutputType> implements ChangingValueBuilder<OutputType> {

    private final ChangingValue<SourceType> sourceValue;
    private final Function<FailableValue<SourceType>, ? extends OutputType> valueMapper;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueIf<? super OutputType>> replaceOldValueIf = Optional.empty();
    private Optional<Consumer<? super OutputType>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<? super OutputType>> disposeFunction = Optional.empty();
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
                new ChangingValueUpdateConfig<>(
                        replaceOldValueIf,
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

    public DerivedValueBuilder<SourceType, OutputType> withReplaceOldValueIf(ReplaceOldValueIf<? super OutputType> replaceOldValueIf) {
        this.replaceOldValueIf = Optional.of(replaceOldValueIf);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withAfterValueChangedFunction(Consumer<? super OutputType> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withDisposeFunction(Consumer<? super OutputType> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public DerivedValueBuilder<SourceType, OutputType> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
