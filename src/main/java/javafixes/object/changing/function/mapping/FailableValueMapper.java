package javafixes.object.changing.function.mapping;

import javafixes.object.changing.FailableValue;

import java.util.function.Function;

@FunctionalInterface
public interface FailableValueMapper<SourceType, OutputType> {

    OutputType map(FailableValue<SourceType> value);


    static <SourceType, OutputType> FailableValueMapper<SourceType, OutputType> value(
            Function<? super SourceType, ? extends OutputType> valueMapper
    ) {
        return failableValue -> valueMapper.apply(failableValue.value());
    }
}
