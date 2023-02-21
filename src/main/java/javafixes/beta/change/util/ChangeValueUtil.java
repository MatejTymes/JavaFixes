package javafixes.beta.change.util;

import javafixes.beta.change.FailableValue;
import javafixes.beta.change.function.ShouldReplaceOldValueCheck;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ChangeValueUtil {

    public static <SourceType, OutputType> Function<FailableValue<SourceType>, ? extends OutputType> mappingValue(
            Function<SourceType, ? extends OutputType> valueMapper
    ) {
        return failableValue -> valueMapper.apply(failableValue.value());
    }

    public static <T> ShouldReplaceOldValueCheck<T> comparingValues(
            BiFunction<T, T, Boolean> valueCheck
    ) {
        return (oldValue, newValue) -> valueCheck.apply(oldValue.value(), newValue.value());
    }
}
