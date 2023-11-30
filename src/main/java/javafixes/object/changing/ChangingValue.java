package javafixes.object.changing;

import javafixes.common.function.TriFunction;
import javafixes.common.function.ValueHandler;
import javafixes.common.function.ValueMapper;
import javafixes.object.Value;
import javafixes.object.changing.builder.CachedValueBuilder;
import javafixes.object.changing.builder.DerivedJoinedValueBuilder;
import javafixes.object.changing.builder.DerivedValueBuilder;
import javafixes.object.changing.function.mapping.FailableValueMapper;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static javafixes.object.changing.builder.CachedValueBuilder.cachedValueBuilder;
import static javafixes.object.changing.builder.DerivedValueBuilder.derivedValue;
import static javafixes.object.changing.builder.DerivedValueBuilder.derivedValueBuilder;
import static javafixes.object.changing.util.ChangingValueUtil.joiningValues;

/**
 * {@code ChangingValue} is intended as wrapper of value that could change over time and whose changes should propagate
 * to other values derived from it automatically.
 * <p>
 * <p>A good usage example would be a config from which other values could be derived and should be changed
 * once config changes.
 *
 * @param <T> type of wrapped value
 * @author mtymes
 */
// todo: mtymes - add javadoc to all implementations
public interface ChangingValue<T> extends Value<T> {

    /**
     * @return optional name of {@code ChangingValue}
     */
    Optional<String> name();

    /**
     * Provides currently wrapped value with its change version number
     *
     * @return current {@code ChangingValue}'s wrapped {@code VersionedValue}
     */
    VersionedValue<T> versionedValue();

    /**
     * @return current {@code ChangingValue}'s wrapped value
     * @throws RuntimeException in case the wrapped value is a failure
     */
    @Override
    default T value() {
        return versionedValue().value();
    }

    /**
     * Indicator of how often has the wrapped value changed since initialization.
     * Should be 0 when value is created and each time the underlying value changes the {@code changeVersion} should
     * increase by one.
     *
     * @return number indicator of how many times has the wrapped value changed
     */
    default long changeVersion() {
        return versionedValue().versionNumber;
    }

    /**
     * @return current {@code ChangingValue}'s wrapped {@code FailableValue}
     */
    default FailableValue<T> failableValue() {
        return versionedValue().failableValue();
    }

    /**
     * Generates a value using the current {@code ChangingValue}'s wrapped value
     *
     * @param valueMapper function that is applied to currently wrapped value
     * @param <T2>        type of generated value
     * @param <E>         {@link Throwable} type, in case the {@code valueMapper} function should throw an {@link Throwable}
     * @return generated value
     * @throws E exception generated by {@code valueMapper} function
     */
    default <T2, E extends Throwable> T2 mapCurrentValue(ValueMapper<? super T, ? extends T2, E> valueMapper) throws E {
        return valueMapper.map(value());
    }

    /**
     * Executes {@code valueHandler} function for currently wrapped value
     *
     * @param valueHandler function that is executed for currently wrapped value
     * @param <E>          {@link Throwable} type, in case the {@code valueHandler} function should throw an {@link Throwable}
     * @throws E exception generated by {@code valueHandler} function
     */
    default <E extends Throwable> void forCurrentValue(ValueHandler<? super T, ? extends E> valueHandler) throws E {
        valueHandler.handle(value());
    }

    // map functions

    default <T2> DerivedValue<T, T2> map(FailableValueMapper<T, T2> valueMapper) {
        return derivedValue(this, valueMapper);
    }

    default <T2> DerivedValue<T, T2> mapValue(Function<? super T, ? extends T2> valueMapper) {
        return derivedValue(this, FailableValueMapper.value(valueMapper));
    }

    default <T2> DerivedValueBuilder<T, T2> mapBuilder(FailableValueMapper<T, T2> valueMapper) {
        return derivedValueBuilder(this, valueMapper);
    }

    default <T2> DerivedValueBuilder<T, T2> mapValueBuilder(Function<? super T, ? extends T2> valueMapper) {
        return derivedValueBuilder(this, FailableValueMapper.value(valueMapper));
    }

    // join functions

    default <T2, OutputType> DerivedJoinedValue<OutputType> join(
            ChangingValue<T2> value2,
            BiFunction<FailableValue<T>, FailableValue<T2>, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.join(this, value2, mapFunction);
    }

    default <T2, T3, OutputType> DerivedJoinedValue<OutputType> join(
            ChangingValue<T2> value2,
            ChangingValue<T3> value3,
            TriFunction<FailableValue<T>, FailableValue<T2>, FailableValue<T3>, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.join(this, value2, value3, mapFunction);
    }

    default <T2, OutputType> DerivedJoinedValue<OutputType> joinValues(
            ChangingValue<T2> value2,
            BiFunction<T, T2, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.join(this, value2, joiningValues(mapFunction));
    }

    default <T2, T3, OutputType> DerivedJoinedValue<OutputType> joinValues(
            ChangingValue<T2> value2,
            ChangingValue<T3> value3,
            TriFunction<T, T2, T3, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.join(this, value2, value3, joiningValues(mapFunction));
    }

    default <T2, OutputType> DerivedJoinedValueBuilder<OutputType> joinBuilder(
            ChangingValue<T2> value2,
            BiFunction<FailableValue<T>, FailableValue<T2>, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.joinBuilder(this, value2, mapFunction);
    }

    default <T2, T3, OutputType> DerivedJoinedValueBuilder<OutputType> joinBuilder(
            ChangingValue<T2> value2,
            ChangingValue<T3> value3,
            TriFunction<FailableValue<T>, FailableValue<T2>, FailableValue<T3>, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.joinBuilder(this, value2, value3, mapFunction);
    }

    default <T2, OutputType> DerivedJoinedValueBuilder<OutputType> joinValuesBuilder(
            ChangingValue<T2> value2,
            BiFunction<T, T2, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.joinBuilder(this, value2, joiningValues(mapFunction));
    }

    default <T2, T3, OutputType> DerivedJoinedValueBuilder<OutputType> joinValuesBuilder(
            ChangingValue<T2> value2,
            ChangingValue<T3> value3,
            TriFunction<T, T2, T3, OutputType> mapFunction
    ) {
        return DerivedJoinedValueBuilder.joinBuilder(this, value2, value3, joiningValues(mapFunction));
    }

    // cache functions

    default CachedValueBuilder<T> cacheBuilder() {
        return cachedValueBuilder(this);
    }
}
