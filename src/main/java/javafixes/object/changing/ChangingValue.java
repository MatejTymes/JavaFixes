package javafixes.object.changing;

import javafixes.common.function.ValueHandler;
import javafixes.common.function.ValueMapper;
import javafixes.object.Triple;
import javafixes.object.Tuple;
import javafixes.object.Value;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.reflect.Proxy.newProxyInstance;
import static javafixes.collection.CollectionUtil.newList;
import static javafixes.object.Triple.triple;
import static javafixes.object.Tuple.tuple;

/**
 * {@code ChangingValue} is intended as wrapper of value that could change over time and whose changes should propagate
 * to other values automatically.
 * <p>
 * <p>A good usage example would be a config from which other values could be derived and should be changed
 * once config changes.
 *
 * @param <T> type of wrapped value
 * @author mtymes
 */
// todo: add withOnUpdateFunction(BiConsumer<T, T> onUpdate) method
// todo: test default and static methods
// todo: add null check for input parameters
public interface ChangingValue<T> extends Value<T> {

    /**
     * @return optional name of {@code ChangingValue}
     */
    Optional<String> name();

    /**
     * Indicator of how often has the wrapped value changed.
     * Should be 0 when value is created and each time the underlying value changes the {@code changeVersion} should
     * increase by one.
     *
     * @return number indicator of how many times has the wrapped value changed
     */
    long changeVersion();

    /**
     * Creates a new {@link DerivedValue} from this {@code ChangingValue}.
     * Each time the generated {@link DerivedValue} will be used and this value will change, the {@link DerivedValue}
     * will regenerate it's value using the provided {@code derivedValueMapper}
     *
     * @param derivedValueMapper function that is applied to this {@code ChangingValue}'s wrapped value to generate new {@link DerivedValue}'s value
     * @param <T2>               type of newly generated {@link DerivedValue}
     * @return generated {@link DerivedValue}
     */
    default <T2> DerivedValue<T2, T> map(Function<T, ? extends T2> derivedValueMapper) {
        return new DerivedValue<>(
                Optional.empty(),
                this,
                derivedValueMapper,
                Optional.empty(),
                Optional.empty(),
                false
        );
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
    default <T2, E extends Throwable> T2 mapToValue(ValueMapper<? super T, ? extends T2, E> valueMapper) throws E {
        return valueMapper.map(value());
    }

    default <TI> TI mapToProxy(Class<TI> proxyInterface) {
        return (TI) newProxyInstance(
                ChangingValueUtil.class.getClassLoader(),
                new Class[]{proxyInterface},
                (proxy, method, args) -> {
                    Object value = this.value();
                    try {
                        return method.invoke(value, args);
                    } catch (InvocationTargetException invocationTargetException) {
                        throw invocationTargetException.getCause();
                    }
                }
        );
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

    /**
     * Merges two {@code ChangingValue}s of type {@code T1} and {@code T2} into one {@code DerivedValue} of type {@code Tuple<T1, T2>}
     *
     * @param value1 first {@code ChangingValue} of type {@code T1}
     * @param value2 second {@code ChangingValue} of type {@code T2}
     * @param <T1>   type of {@code value1}
     * @param <T2>   type of {@code value2}
     * @return {@code DerivedValue} of type {@code Tuple<T1, T2>}
     */
    static <T1, T2> DerivedValue<Tuple<T1, T2>, ?> join(ChangingValue<T1> value1, ChangingValue<T2> value2) {
        return ((ChangingValue<List<Object>>) new DerivedMultiValue(newList(value1, value2)))
                .map(values -> tuple(
                        (T1) values.get(0),
                        (T2) values.get(1)
                ));
    }

    /**
     * Merges three {@code ChangingValue}s of type {@code T1}, {@code T2} and {@code T3} into one {@code DerivedValue} of type {@code Triple<T1, T2, T3>}
     *
     * @param value1 first {@code ChangingValue} of type {@code T1}
     * @param value2 second {@code ChangingValue} of type {@code T2}
     * @param value3 third {@code ChangingValue} of type {@code T3}
     * @param <T1>   type of {@code value1}
     * @param <T2>   type of {@code value2}
     * @param <T3>   type of {@code value3}
     * @return {@code DerivedValue} of type {@code Triple<T1, T2, T3>}
     */
    static <T1, T2, T3> DerivedValue<Triple<T1, T2, T3>, ?> join(ChangingValue<T1> value1, ChangingValue<T2> value2, ChangingValue<T3> value3) {
        return ((ChangingValue<List<Object>>) new DerivedMultiValue(newList(value1, value2, value3)))
                .map(values -> triple(
                        (T1) values.get(0),
                        (T2) values.get(1),
                        (T3) values.get(2)
                ));
    }

    /**
     * Merges a {@link List} of {@code ChangingValue}s of type {@code T} into one {@code ChangingValue} of type {@code List<T>}
     *
     * @param values changing values to merge
     * @param <T>    type of {@code values} {@link List}
     * @return {@code ChangingValue} of type {@code List<T>}
     */
    static <T> ChangingValue<List<T>> join(List<ChangingValue<T>> values) {
        return new DerivedMultiValue<>(values);
    }
}
