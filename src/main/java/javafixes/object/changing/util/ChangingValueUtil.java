package javafixes.object.changing.util;

import javafixes.common.function.TriFunction;
import javafixes.common.function.ValueMapper;
import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.FailableValue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.stream.Collectors.toList;

public class ChangingValueUtil {

    public static <ProxyInterface, T extends ProxyInterface> ProxyInterface mapToProxyInterface(
            ChangingValue<T> changingValue,
            Class<ProxyInterface> proxyInterfaceClass
    ) {
        return (ProxyInterface) newProxyInstance(
                ChangingValueUtil.class.getClassLoader(),
                new Class[]{proxyInterfaceClass},
                (proxy, method, args) -> {
                    Object value = changingValue.value();
                    try {
                        return method.invoke(value, args);
                    } catch (InvocationTargetException invocationTargetException) {
                        throw invocationTargetException.getCause();
                    }
                }
        );
    }

    public static <T1, T2, OutputType> BiFunction<FailableValue<T1>, FailableValue<T2>, OutputType> joiningValues(
            BiFunction<? super T1, ? super T2, ? extends OutputType> valuesMapper
    ) {
        return (value1, value2) -> valuesMapper.apply(value1.value(), value2.value());
    }

    public static <T1, T2, T3, OutputType> TriFunction<FailableValue<T1>, FailableValue<T2>, FailableValue<T3>, OutputType> joiningValues(
            TriFunction<? super T1, ? super T2, ? super T3, ? extends OutputType> valuesMapper
    ) {
        return (value1, value2, value3) -> valuesMapper.apply(value1.value(), value2.value(), value3.value());
    }

    public static <T, OutputType> Function<List<FailableValue<T>>, OutputType> joiningValues(
            Function<List<? super T>, ? extends OutputType> valuesMapper
    ) {
        return values -> valuesMapper.apply(values.stream().map(FailableValue::value).collect(toList()));
    }
}
