package javafixes.beta.change;

import javafixes.common.function.ValueHandler;
import javafixes.object.Value;

import java.util.Optional;

public interface ChangingValue<T> extends Value<T> {

    Optional<String> name();

    VersionedValue<T> getVersionedValue();

    @Override
    default T value() {
        return getVersionedValue().value();
    }

    default long changeVersion() {
        return getVersionedValue().versionNumber;
    }

    default <E extends Throwable> void forCurrentValue(ValueHandler<? super T, ? extends E> valueHandler) throws E {
        valueHandler.handle(value());
    }
}
