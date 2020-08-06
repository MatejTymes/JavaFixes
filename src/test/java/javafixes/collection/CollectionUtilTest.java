package javafixes.collection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CollectionUtilTest {

    @Test
    public void shouldCreateArrayListFromArray() {
        ArrayList<String> list = CollectionUtil.newList("Hello", "World", "!");

        assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateArrayListFromIterable() {
        Iterable<String> values = asList("Hello", "World", "!");

        ArrayList<String> list = CollectionUtil.newList(values);

        assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateArrayListFromIterator() {
        Iterator<String> values = asList("Hello", "World", "!").iterator();

        ArrayList<String> list = CollectionUtil.newList(values);

        assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateHashSetFromArray() {
        HashSet<String> list = CollectionUtil.newSet("Hello", "World", "!");

        assertThat(list, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateHashSetFromIterable() {
        Iterable<String> values = asList("Hello", "World", "!");

        HashSet<String> list = CollectionUtil.newSet(values);

        assertThat(list, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateHashSetFromIterator() {
        Iterator<String> values = asList("Hello", "World", "!").iterator();

        HashSet<String> list = CollectionUtil.newSet(values);

        assertThat(list, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateLinkedHashSetFromArray() {
        LinkedHashSet<String> list = CollectionUtil.newLinkedSet("Hello", "World", "!");

        assertThat(list, equalTo(new LinkedHashSet<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateLinkedHashSetFromIterable() {
        Iterable<String> values = asList("Hello", "World", "!");

        LinkedHashSet<String> list = CollectionUtil.newLinkedSet(values);

        assertThat(list, equalTo(new LinkedHashSet<>(asList("Hello", "World", "!"))));
    }

    @Test
    public void shouldCreateLinkedHashSetFromIterator() {
        Iterator<String> values = asList("Hello", "World", "!").iterator();

        LinkedHashSet<String> list = CollectionUtil.newLinkedSet(values);

        assertThat(list, equalTo(new LinkedHashSet<>(asList("Hello", "World", "!"))));
    }
}