package javafixes.experimental.object;

import javafixes.object.Value;

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

    default <T> T asProxy(Class<T> proxyInterface) {
        return (T) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{proxyInterface},
                (proxy, method, args) -> {
                    return method.invoke(value(), args);
                }
        );
    }
}
