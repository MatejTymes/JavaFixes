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

    long getLastRetrievalOfSourceValueTimestamp();

    default ZonedDateTime getLastRetrievalOfSourceValueTime(ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getLastRetrievalOfSourceValueTimestamp()), zoneId);
    }
}
