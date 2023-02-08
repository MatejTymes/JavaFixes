package javafixes.experimental.change;

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
}
