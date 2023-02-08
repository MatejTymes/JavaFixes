package javafixes.experimental.change;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface CachedChangingValue<T> extends ChangingValue<T> {

    void forceNewValueReCaching(boolean ignoreDifferenceCheck);

    default void forceNewValueReCaching() {
        forceNewValueReCaching(false);
    }

    long getLastReCacheTimestamp();

    default ZonedDateTime getLastReCacheTime(ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getLastReCacheTimestamp()), zoneId);
    }
}
