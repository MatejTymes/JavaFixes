package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedJoinedValue;
import javafixes.beta.change.FailableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueIf;
import javafixes.common.function.TriFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedJoinedValueBuilder<T> implements ChangingValueBuilder<T> {

    private final List<ChangingValue<? extends Object>> sourceValues;
    private final Function<List<FailableValue<? super Object>>, ? extends T> valuesMapper;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf = Optional.empty();
    private Optional<Consumer<? super T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<? super T>> disposeFunction = Optional.empty();
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
            BiFunction<FailableValue<? super T1>, FailableValue<? super T2>, ? extends OutputType> mapFunction
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
            TriFunction<FailableValue<? super T1>, FailableValue<? super T2>, FailableValue<? super T3>, ? extends OutputType> mapFunction
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
            Function<List<FailableValue<? super T>>, ? extends OutputType> mapFunction
    ) {
        return new DerivedJoinedValueBuilder<>(values, mapFunction);
    }

    @Override
    public DerivedJoinedValue<T> build() {
        return new DerivedJoinedValue<>(
                valueName,
                sourceValues,
                valuesMapper,
                new ChangingValueUpdateConfig<>(
                        replaceOldValueIf,
                        afterValueChangedFunction,
                        disposeFunction
                ),
                prePopulateValueImmediately
        );
    }

    public DerivedJoinedValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.replaceOldValueIf = Optional.of(replaceOldValueIf);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withAfterValueChangedFunction(Consumer<? super T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
