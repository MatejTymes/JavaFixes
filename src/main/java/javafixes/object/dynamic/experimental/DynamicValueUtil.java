package javafixes.object.dynamic.experimental;

import javafixes.object.dynamic.DynamicValue;

import java.lang.reflect.InvocationTargetException;

import static java.lang.reflect.Proxy.newProxyInstance;

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
}
