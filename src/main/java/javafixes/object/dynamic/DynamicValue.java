package javafixes.object.dynamic;

import javafixes.common.function.ValueHandler;
import javafixes.common.function.ValueMapper;
import javafixes.object.Triple;
import javafixes.object.Tuple;
import javafixes.object.Value;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static javafixes.collection.CollectionUtil.newList;
import static javafixes.object.Triple.triple;
import static javafixes.object.Tuple.tuple;

// todo: add javadoc
// todo: test static methods
public interface DynamicValue<T> extends Value<T> {

    Optional<String> name();

    long valueVersion();

    default <T2> DerivedValue<T2, T> map(Function<T, ? extends T2> derivedValueMapper) {
        return new DerivedValue<>(Optional.empty(), this, derivedValueMapper, Optional.empty());
    }

    default <T2, E extends Throwable> T2 mapToValue(ValueMapper<? super T, ? extends T2, E> valueMapper) throws E {
        return valueMapper.map(value());
    }

    default <E extends Throwable> void handleCurrentValue(ValueHandler<? super T, ? extends E> valueHandler) throws E {
        valueHandler.handle(value());
    }

    static <T1, T2> DynamicValue<Tuple<T1, T2>> join(DynamicValue<T1> value1, DynamicValue<T2> value2) {
        return ((DynamicValue<List<Object>>) new DerivedMultiValue(newList(value1, value2)))
                .map(values -> tuple(
                        (T1) values.get(0),
                        (T2) values.get(1)
                ));
    }

    static <T1, T2, T3> DynamicValue<Triple<T1, T2, T3>> join(DynamicValue<T1> value1, DynamicValue<T2> value2, DynamicValue<T3> value3) {
        return ((DynamicValue<List<Object>>) new DerivedMultiValue(newList(value1, value2, value3)))
                .map(values -> triple(
                        (T1) values.get(0),
                        (T2) values.get(1),
                        (T3) values.get(2)
                ));
    }

    static <T> DynamicValue<List<T>> join(List<DynamicValue<T>> values) {
        return new DerivedMultiValue<>(values);
    }
}
