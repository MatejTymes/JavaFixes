package javafixes.beta.change;

import javafixes.object.DataObject;
import javafixes.object.Value;

import java.util.function.Function;

import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;
import static javafixes.common.Asserts.assertNotNull;

public class VersionedValue<T> extends DataObject implements Value<T> {

    final FailableValue<T> value;
    final long versionNumber;

    private VersionedValue(
            FailableValue<T> value,
            long versionNumber
    ) {
        assertNotNull(value, "value", "VersionedValue");

        this.value = value;
        this.versionNumber = versionNumber;
    }


    @Override
    public T value() {
        return value.value();
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public <T2> T2 fold(Function<T, T2> foldValue, Function<RuntimeException, T2> foldFailure) {
        return value.fold(foldValue, foldFailure);
    }

    public FailableValue<T> failableValue() {
        return value;
    }

    public boolean isFailure() {
        return value.isFailure();
    }


    static <T> VersionedValue<T> initialValueVersion(
            FailableValue<T> value
    ) {
        return new VersionedValue<>(value, 0);
    }

    static <T> VersionedValue<T> initialValueVersion(
            T value
    ) {
        return initialValueVersion(wrapValue(value));
    }

    static <T> VersionedValue<T> initialValueVersion(
            RuntimeException exception
    ) {
        return initialValueVersion(wrapFailure(exception));
    }


    VersionedValue<T> nextVersion(
            FailableValue<T> value
    ) {
        return new VersionedValue<>(
                value,
                versionNumber + 1
        );
    }

    VersionedValue<T> nextVersion(
            T value
    ) {
        return nextVersion(wrapValue(value));
    }

    VersionedValue<T> nextVersion(
            RuntimeException exception
    ) {
        return nextVersion(wrapFailure(exception));
    }
}
