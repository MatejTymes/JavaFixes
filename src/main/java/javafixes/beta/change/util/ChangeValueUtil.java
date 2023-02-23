package javafixes.beta.change.util;

import javafixes.beta.change.FailableValue;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;
import javafixes.common.function.TriFunction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ChangeValueUtil {

    public static <SourceType, OutputType> Function<FailableValue<SourceType>, ? extends OutputType> mappingValue(
            Function<? super SourceType, ? extends OutputType> valueMapper
    ) {
        return failableValue -> valueMapper.apply(failableValue.value());
    }

    public static <T> ReplaceOldValueIf<T> comparingValues(
            BiFunction<T, T, Boolean> valueCheck
    ) {
        return (oldValue, newValue) -> valueCheck.apply(oldValue.value(), newValue.value());
    }

    public static <T> FailableValueHandler<T> handleValue(
            Consumer<T> consumer
    ) {
        return failableValue -> failableValue.handleValue(consumer);
    }

    public static <T> FailableValueHandler<T> handleFailure(
            Consumer<RuntimeException> consumer
    ) {
        return failableValue -> failableValue.handleFailure(consumer);
    }

    public static <T1, T2, OutputType> BiFunction<FailableValue<T1>, FailableValue<T2>, ? extends OutputType> joiningValues(
            BiFunction<? super T1, ? super T2, ? extends OutputType> valuesMapper
    ) {
        return (value1, value2) -> valuesMapper.apply(value1.value(), value2.value());
    }

    public static <T1, T2, T3, OutputType> TriFunction<FailableValue<T1>, FailableValue<T2>, FailableValue<T3>, ? extends OutputType> joiningValues(
            TriFunction<? super T1, ? super T2, ? super T3, ? extends OutputType> valuesMapper
    ) {
        return (value1, value2, value3) -> valuesMapper.apply(value1.value(), value2.value(), value3.value());
    }

    public static <T, OutputType> Function<List<FailableValue<T>>, ? extends OutputType> joiningValues(
            Function<List<? super T>, ? extends OutputType> valuesMapper
    ) {
        return values -> valuesMapper.apply(values.stream().map(FailableValue::value).collect(toList()));
    }
}
