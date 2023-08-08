package javafixes.common.util;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// todo: add javadoc
// todo: test this
public class StreamUtil {

    public static <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> toStream(Iterator<T> iterator) {
        return toStream(() -> iterator);
    }
}
