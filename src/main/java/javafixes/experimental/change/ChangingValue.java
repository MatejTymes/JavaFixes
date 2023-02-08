package javafixes.experimental.change;

import javafixes.object.Value;

public interface ChangingValue<T> extends Value<T> {

    VersionedValue<T> getVersionedValue();

    @Override
    default T value() {
        return getVersionedValue().value();
    }

    default long changeVersion() {
        return getVersionedValue().versionNumber;
    }
}
