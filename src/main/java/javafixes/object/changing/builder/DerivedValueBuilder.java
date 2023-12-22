package javafixes.object.changing.builder;

import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.DerivedValue;
import javafixes.object.changing.function.mapping.FailableValueMapper;

public class DerivedValueBuilder<OutputType> extends AbstractChangingValueBuilder<OutputType, DerivedValueBuilder<OutputType>> {

    private final ChangingValue<?> sourceValue;
    private final FailableValueMapper<?, ? extends OutputType> valueMapper;

    private boolean prePopulateValueImmediately = false;

    public <SourceType> DerivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
    }


    public static <SourceType, OutputType> DerivedValueBuilder<OutputType> derivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        return new DerivedValueBuilder<>(sourceValue, valueMapper);
    }

    public static <SourceType, OutputType> DerivedValueBuilder<OutputType> derivedValueBuilder(
            ChangingValueBuilder<SourceType> sourceValueBuilder,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        return derivedValueBuilder(sourceValueBuilder.build(), valueMapper);
    }


    public static <SourceType, OutputType> DerivedValue<OutputType> derivedValue(
            ChangingValue<SourceType> sourceValue,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        DerivedValueBuilder<OutputType> builder = derivedValueBuilder(sourceValue, valueMapper);
        return builder.build();
    }

    public static <SourceType, OutputType> DerivedValue<OutputType> derivedValue(
            ChangingValueBuilder<SourceType> sourceValueBuilder,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper
    ) {
        DerivedValueBuilder<OutputType> builder = derivedValueBuilder(sourceValueBuilder, valueMapper);
        return builder.build();
    }


    @Override
    public DerivedValue<OutputType> build() {
        return new DerivedValue<>(
                valueName,
                (ChangingValue<Object>) sourceValue,
                updateConfig(),
                (FailableValueMapper<Object, ? extends OutputType>) valueMapper,
                prePopulateValueImmediately
        );
    }

    public DerivedValueBuilder<OutputType> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }

    @Override
    protected DerivedValueBuilder<OutputType> thisBuilder() {
        return this;
    }
}
