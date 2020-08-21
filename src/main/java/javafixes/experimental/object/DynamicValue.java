package javafixes.experimental.object;

import javafixes.common.function.ValueHandler;
import javafixes.object.Value;

import java.util.Optional;
import java.util.function.Function;

// todo: add javadoc
public interface DynamicValue<T> extends Value<T> {

    Optional<String> name();

    long valueVersion();

    default <T2> DerivedValue<T2, T> map(Function<T, ? extends T2> derivedValueMapper) {
        return new DerivedValue<>(Optional.empty(), this, derivedValueMapper, Optional.empty());
    }

    default <E extends Throwable> void handleCurrentValue(ValueHandler<? super T, ? extends E> valueHandler) throws E {
        valueHandler.handle(value());
    }
}
