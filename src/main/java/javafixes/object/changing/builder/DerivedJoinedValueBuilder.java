package javafixes.object.changing.builder;

import javafixes.common.function.TriFunction;
import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.DerivedJoinedValue;
import javafixes.object.changing.FailableValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DerivedJoinedValueBuilder<T> extends AbstractChangingValueBuilder<T, DerivedJoinedValueBuilder<T>> {

    private final List<ChangingValue<? extends Object>> sourceValues;
    private final Function<List<FailableValue<? super Object>>, ? extends T> valuesMapper;

    private boolean prePopulateValueImmediately = false;

    public <SourceType> DerivedJoinedValueBuilder(
            List<ChangingValue<? extends SourceType>> sourceValues,
            Function<List<FailableValue<? super SourceType>>, ? extends T> valuesMapper
    ) {
        this.sourceValues = new ArrayList<>(sourceValues);
        this.valuesMapper = (Function) valuesMapper;
    }


    public static <T1, T2, OutputType> DerivedJoinedValueBuilder<OutputType> joinBuilder(
            ChangingValue<T1> value1,
            ChangingValue<T2> value2,
            BiFunction<FailableValue<T1>, FailableValue<T2>, OutputType> mapFunction
    ) {
        return new DerivedJoinedValueBuilder<>(
                Arrays.asList(value1, value2),
                values -> {
                    FailableValue<T1> val1 = (FailableValue) values.get(0);
                    FailableValue<T2> val2 = (FailableValue) values.get(1);
                    return mapFunction.apply(val1, val2);
                }
        );
    }

    public static <T1, T2, T3, OutputType> DerivedJoinedValueBuilder<OutputType> joinBuilder(
            ChangingValue<T1> value1,
            ChangingValue<T2> value2,
            ChangingValue<T3> value3,
            TriFunction<FailableValue<T1>, FailableValue<T2>, FailableValue<T3>, OutputType> mapFunction
    ) {
        return new DerivedJoinedValueBuilder<>(
                Arrays.asList(value1, value2, value3),
                values -> {
                    FailableValue<T1> val1 = (FailableValue) values.get(0);
                    FailableValue<T2> val2 = (FailableValue) values.get(1);
                    FailableValue<T3> val3 = (FailableValue) values.get(2);
                    return mapFunction.apply(val1, val2, val3);
                }
        );
    }

    public static <T, OutputType> DerivedJoinedValueBuilder<OutputType> joinBuilder(
            List<ChangingValue<? extends T>> values,
            Function<List<FailableValue<? super T>>, OutputType> mapFunction
    ) {
        return new DerivedJoinedValueBuilder<>(values, mapFunction);
    }


    public static <T1, T2, OutputType> DerivedJoinedValue<OutputType> join(
            ChangingValue<T1> value1,
            ChangingValue<T2> value2,
            BiFunction<FailableValue<T1>, FailableValue<T2>, OutputType> mapFunction
    ) {
        return joinBuilder(value1, value2, mapFunction).build();
    }

    public static <T1, T2, T3, OutputType> DerivedJoinedValue<OutputType> join(
            ChangingValue<T1> value1,
            ChangingValue<T2> value2,
            ChangingValue<T3> value3,
            TriFunction<FailableValue<T1>, FailableValue<T2>, FailableValue<T3>, OutputType> mapFunction
    ) {
        return joinBuilder(value1, value2, value3, mapFunction).build();
    }

    public static <T, OutputType> DerivedJoinedValue<OutputType> join(
            List<ChangingValue<? extends T>> values,
            Function<List<FailableValue<? super T>>, OutputType> mapFunction
    ) {
        return joinBuilder(values, mapFunction).build();
    }


    @Override
    public DerivedJoinedValue<T> build() {
        return new DerivedJoinedValue<>(
                valueName,
                sourceValues,
                updateConfig(),
                valuesMapper,
                prePopulateValueImmediately
        );
    }

    public DerivedJoinedValueBuilder<T> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }

    @Override
    protected DerivedJoinedValueBuilder<T> thisBuilder() {
        return this;
    }
}
