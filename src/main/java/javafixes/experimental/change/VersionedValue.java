package javafixes.experimental.change;

import javafixes.object.DataObject;
import javafixes.object.Either;
import javafixes.object.Value;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

public class VersionedValue<T> extends DataObject implements Value<T> {

    final Either<RuntimeException, T> value;
    final long versionNumber;

    private VersionedValue(
            Either<RuntimeException, T> value,
            long versionNumber
    ) {
        this.value = value;
        this.versionNumber = versionNumber;
    }


    @Override
    public T value() {
        return value
                .ifLeftThrow(e -> e)
                .getRight();
    }

    public long getVersionNumber() {
        return versionNumber;
    }


    static <T> VersionedValue<T> initialValueVersion(
            Either<RuntimeException, T> value
    ) {
        return new VersionedValue<>(value, 0);
    }

    static <T> VersionedValue<T> initialValueVersion(
            T value
    ) {
        return initialValueVersion(right(value));
    }

    static <T> VersionedValue<T> initialValueVersion(
            RuntimeException exception
    ) {
        return initialValueVersion(left(exception));
    }

    VersionedValue<T> nextVersion(
            Either<RuntimeException, T> value
    ) {
        return new VersionedValue<>(
                value,
                versionNumber + 1
        );
    }

    VersionedValue<T> nextVersion(
            T value
    ) {
        return nextVersion(right(value));
    }

    VersionedValue<T> nextVersion(
            RuntimeException exception
    ) {
        return nextVersion(left(exception));
    }
}
