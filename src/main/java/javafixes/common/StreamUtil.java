package javafixes.common;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// todo: use better package
// todo: add javadoc
public class StreamUtil {

    public static <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> toStream(Iterator<T> iterator) {
        return toStream(() -> iterator);
    }
}
