package javafixes.experimental.object;

import javafixes.object.Triple;
import javafixes.object.Tuple;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.lang.reflect.Proxy.newProxyInstance;
import static javafixes.collection.CollectionUtil.newList;
import static javafixes.object.Triple.triple;
import static javafixes.object.Tuple.tuple;

// todo: test
// todo: javadoc
public class DynamicValueUtil {

    public static <TI> TI mapToProxy(Class<TI> proxyInterface, DynamicValue<?> dynamicValue) {
        return (TI) newProxyInstance(
                DynamicValueUtil.class.getClassLoader(),
                new Class[]{proxyInterface},
                (proxy, method, args) -> {
                    Object value = dynamicValue.value();
                    try {
                        return method.invoke(value, args);
                    } catch (InvocationTargetException invocationTargetException) {
                        throw invocationTargetException.getCause();
                    }
                }
        );
    }

    public static <T1, T2> DynamicValue<Tuple<T1, T2>> join(DynamicValue<T1> value1, DynamicValue<T2> value2) {
        return ((DynamicValue<List<Object>>) new DerivedMultiValue(newList(value1, value2)))
                .map(values -> tuple(
                        (T1) values.get(0),
                        (T2) values.get(1)
                ));
    }

    public static <T1, T2, T3> DynamicValue<Triple<T1, T2, T3>> join(DynamicValue<T1> value1, DynamicValue<T2> value2, DynamicValue<T3> value3) {
        return ((DynamicValue<List<Object>>) new DerivedMultiValue(newList(value1, value2, value3)))
                .map(values -> triple(
                        (T1) values.get(0),
                        (T2) values.get(1),
                        (T3) values.get(2)
                ));
    }

    public static <T> DynamicValue<List<T>> join(List<DynamicValue<T>> values) {
        return new DerivedMultiValue<>(values);
    }
}
