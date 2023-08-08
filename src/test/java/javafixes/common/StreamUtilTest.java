package javafixes.common;

import javafixes.common.util.StreamUtil;
import org.junit.Test;

import java.util.Iterator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static javafixes.collection.util.CollectionUtil.newList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class StreamUtilTest {

    @Test
    public void shouldCreateStreamFromIterable() {
        Iterable<String> iterable = newList("Hello", "World", "!");

        Stream<String> stream = StreamUtil.toStream(iterable);

        assertThat(stream.collect(toList()), equalTo(newList("Hello", "World", "!")));
    }

    @Test
    public void shouldCreateStreamFromIterator() {
        Iterator<String> iterator = newList("Hello", "World", "!").iterator();

        Stream<String> stream = StreamUtil.toStream(iterator);

        assertThat(stream.collect(toList()), equalTo(newList("Hello", "World", "!")));
    }
}