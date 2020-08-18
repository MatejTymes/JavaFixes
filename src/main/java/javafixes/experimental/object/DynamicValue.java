package javafixes.experimental.object;

import javafixes.object.Value;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.reflect.Proxy.newProxyInstance;

// todo: add javadoc
public interface DynamicValue<T> extends Value<T> {

    Optional<String> name();

    long valueVersion();

    default <T2> DerivedValue<T2, T> map(Function<T, ? extends T2> derivedValueMapper) {
        return new DerivedValue<>(Optional.empty(), this, derivedValueMapper, Optional.empty());
    }

    default <TI> TI asProxyOfType(Class<TI> proxyInterface) {
        return (TI) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{proxyInterface},
                (proxy, method, args) -> {
                    try {
                        return method.invoke(value(), args);
                    } catch (InvocationTargetException invocationTargetException) {
                        throw invocationTargetException.getCause();
                    }
                }
        );
    }
}
