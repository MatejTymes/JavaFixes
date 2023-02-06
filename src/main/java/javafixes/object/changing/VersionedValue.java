package javafixes.object.changing;

import javafixes.object.DataObject;
import javafixes.object.Either;
import javafixes.object.Value;

import java.util.Objects;

import static javafixes.object.Either.right;

/**
 * For INTERNAL USE ONLY - used within ChangingValue
 * @param <T>
 */
class VersionedValue<T> extends DataObject implements Value<T> {

    public final Either<RuntimeException, T> value;
    public final long timestamp;
    public final long versionNumber;

    VersionedValue(
            Either<RuntimeException, T> value,
            long timestamp,
            long versionNumber
    ) {
        this.value = value;
        this.timestamp = timestamp;
        this.versionNumber = versionNumber;
    }

    VersionedValue(
            Either<RuntimeException, T> value,
            long versionNumber
    ) {
        this(value, System.currentTimeMillis(), versionNumber);
    }

    VersionedValue(
            Either<RuntimeException, T> value
    ) {
        this(value, 0);
    }

    public VersionedValue<T> generateNextVersion(T newValue, long newTimestamp) {
        return new VersionedValue<>(
                right(newValue),
                newTimestamp,
                versionNumber + 1
        );
    }

    public VersionedValue<T> generateNextVersion(T newValue) {
        return generateNextVersion(newValue, System.currentTimeMillis());
    }

    public VersionedValue<T> generateNextVersionIfDifferent(T potentialNewValue, long newTimestamp) {
        boolean shouldUpdate = value.fold(
                ifException -> true,
                oldValue -> !Objects.equals(oldValue, potentialNewValue)
        );

        if (!shouldUpdate) {
            return null;
        }

        return generateNextVersion(potentialNewValue, newTimestamp);
    }

    public VersionedValue<T> generateNextVersionIfDifferent(T potentialNewValue) {
        return generateNextVersionIfDifferent(potentialNewValue, System.currentTimeMillis());
    }

    public VersionedValue<T> generateNextVersionAsFailure(RuntimeException exception, long newTimestamp) {
        return new VersionedValue<>(
                Either.left(exception),
                newTimestamp,
                versionNumber + 1
        );
    }

    public VersionedValue<T> generateNextVersionAsFailure(RuntimeException exception) {
        return generateNextVersionAsFailure(exception, System.currentTimeMillis());
    }


    @Override
    public T value() {
        return value
                .ifLeftThrow(e -> e)
                .getRight();
    }

//    public boolean isFailure() {
//        return value.isLeft();
//    }
//
//    public long getTimestamp() {
//        return timestamp;
//    }
//
//    public ZonedDateTime getTimestamp(ZoneId zoneId) {
//        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
//    }
//
//    public long getVersionNumber() {
//        return versionNumber;
//    }
}
