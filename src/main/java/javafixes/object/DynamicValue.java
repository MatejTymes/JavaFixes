package javafixes.object;

import java.util.Optional;
import java.util.function.Function;

// todo: add javadoc
public interface DynamicValue<T> extends Value<T> {

    Optional<String> name();

    long valueVersion();

    <T2> DynamicValue<T2> map(Function<T, ? extends T2> mapper);

    <T2> DynamicValue<T2> map(String derivedValueName, Function<T, ? extends T2> mapper);
}
