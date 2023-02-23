package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedValue;
import javafixes.beta.change.FailableValue;

import java.util.function.Function;

public class DerivedValueBuilder<SourceType, OutputType> extends AbstractChangingValueBuilder<OutputType, DerivedValueBuilder<SourceType, OutputType>> {

    private final ChangingValue<SourceType> sourceValue;
    private final Function<FailableValue<SourceType>, ? extends OutputType> valueMapper;

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

    public DerivedValueBuilder<SourceType, OutputType> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }

    @Override
    protected DerivedValueBuilder<SourceType, OutputType> thisBuilder() {
        return this;
    }
}
