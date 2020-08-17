package javafixes.experimental.object;

import javafixes.object.Value;

import java.util.Optional;
import java.util.function.Function;

// todo: add javadoc
public interface DynamicValue<T> extends Value<T> {

    Optional<String> name();

    long valueVersion();

    default <T2> DerivedValueBuilder usingMapFunction(Function<T, ? extends T2> mapper) {
        return new DerivedValueBuilder(this, mapper);
    }
}
