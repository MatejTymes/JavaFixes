package javafixes.beta.change;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// todo: mtymes - is there some way how to unify the implementation of this interface
public interface CachedChangingValue<T> extends ChangingValue<T> {

    // todo: mtymes - maybe add option to invalidate the cache

    void forceNewValueReCaching(boolean ignoreDifferenceCheck);

    default void forceNewValueReCaching() {
        forceNewValueReCaching(false);
    }

    long getLastCachingTimestamp();

    default ZonedDateTime getLastCachingTimle(ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getLastCachingTimestamp()), zoneId);
    }
}
