package javafixes.object.changing.experimental;

import javafixes.object.changing.ChangingValue;

import java.lang.reflect.InvocationTargetException;

import static java.lang.reflect.Proxy.newProxyInstance;

// todo: test
// todo: javadoc
public class ChangingValueUtil {

    public static <TI> TI mapToProxy(Class<TI> proxyInterface, ChangingValue<?> changingValue) {
        return (TI) newProxyInstance(
                ChangingValueUtil.class.getClassLoader(),
                new Class[]{proxyInterface},
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
}
