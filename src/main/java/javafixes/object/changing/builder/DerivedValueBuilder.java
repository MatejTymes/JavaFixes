package javafixes.object.changing.builder;

import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.DerivedValue;
import javafixes.object.changing.function.mapping.FailableValueMapper;

public class DerivedValueBuilder<SourceType, OutputType> extends AbstractChangingValueBuilder<OutputType, DerivedValueBuilder<SourceType, OutputType>> {

    private final ChangingValue<SourceType> sourceValue;
    private final FailableValueMapper<SourceType, ? extends OutputType> valueMapper;

    private boolean prePopulateValueImmediately = false;

    public DerivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
    }


    public static <SourceType, OutputType> DerivedValueBuilder<SourceType, OutputType> derivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        return new DerivedValueBuilder<>(sourceValue, valueMapper);
    }

    public static <SourceType, OutputType> DerivedValueBuilder<SourceType, OutputType> derivedValueBuilder(
            ChangingValueBuilder<SourceType> sourceValueBuilder,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        return derivedValueBuilder(sourceValueBuilder.build(), valueMapper);
    }


    public static <SourceType, OutputType> DerivedValue<SourceType, OutputType> derivedValue(
            ChangingValue<SourceType> sourceValue,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        DerivedValueBuilder<SourceType, OutputType> builder = derivedValueBuilder(sourceValue, valueMapper);
        return builder.build();
    }

    public static <SourceType, OutputType> DerivedValue<SourceType, OutputType> derivedValue(
            ChangingValueBuilder<SourceType> sourceValueBuilder,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        DerivedValueBuilder<SourceType, OutputType> builder = derivedValueBuilder(sourceValueBuilder, valueMapper);
        return builder.build();
    }


    @Override
    public DerivedValue<SourceType, OutputType> build() {
        return new DerivedValue<>(
                valueName,
                sourceValue,
                updateConfig(),
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
