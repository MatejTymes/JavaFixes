package javafixes.collection;

import javafixes.collection.util.CollectionUtil;
import javafixes.object.Tuple;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static javafixes.collection.util.CollectionUtil.newList;
import static javafixes.object.Tuple.tuple;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class CollectionUtilTest {

    @Test
    public void shouldCreateArrayListFromArray() {
        // modifiable list
        {
            ArrayList<String> list = newList("Hello", "World", "!");
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));

            // can be expanded
            list.set(2, "Oh");
            list.add("Beautiful");
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "Oh", "Beautiful"))));
        }

        // unmodifiable list
        {
            List<String> list = CollectionUtil.unmodifiableListCopy("Hello", "World", "!");
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));

            // can not be expanded
            try {
                list.set(2, "Oh");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                list.add("Beautiful");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));
        }
    }

    @Test
    public void shouldCreateArrayListFromIterable() {
        Iterable<String> values = asList("Hello", "World", "!");

        // modifiable list
        {
            ArrayList<String> list = newList(values);
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));

            // can be expanded
            list.set(2, "Oh");
            list.add("Beautiful");
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "Oh", "Beautiful"))));
        }

        // unmodifiable list
        {
            List<String> list = CollectionUtil.unmodifiableListCopy(values);
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));

            // can not be expanded
            try {
                list.set(2, "Oh");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                list.add("Beautiful");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));
        }
    }

    @Test
    public void shouldCreateArrayListFromIterator() {
        // modifiable list
        {
            ArrayList<String> list = newList(asList("Hello", "World", "!").iterator());
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));

            // can be expanded
            list.set(2, "Oh");
            list.add("Beautiful");
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "Oh", "Beautiful"))));
        }

        // unmodifiable list
        {
            List<String> list = CollectionUtil.unmodifiableListCopy(asList("Hello", "World", "!").iterator());
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));

            // can not be expanded
            try {
                list.set(2, "Oh");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                list.add("Beautiful");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(list, equalTo(new ArrayList<>(asList("Hello", "World", "!"))));
        }
    }

    @Test
    public void shouldCreateHashSetFromArray() {
        // modifiable set
        {
            HashSet<String> set = CollectionUtil.newSet("Hello", "World", "!");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));

            // can be expanded
            set.remove("!");
            set.add("Again");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "Again"))));
        }

        // unmodifiable set
        {
            Set<String> set = CollectionUtil.unmodifiableSetCopy("Hello", "World", "!");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));

            // can not be expanded
            try {
                set.remove("!");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                set.add("Again");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
        }
    }

    @Test
    public void shouldCreateHashSetFromIterable() {
        Iterable<String> values = asList("Hello", "World", "!");

        // modifiable set
        {
            HashSet<String> set = CollectionUtil.newSet(values);
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));

            // can be expanded
            set.remove("!");
            set.add("Again");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "Again"))));
        }

        // unmodifiable set
        {
            Set<String> set = CollectionUtil.unmodifiableSetCopy(values);
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));

            // can not be expanded
            try {
                set.remove("!");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                set.add("Again");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
        }
    }

    @Test
    public void shouldCreateHashSetFromIterator() {
        // modifiable set
        {
            HashSet<String> set = CollectionUtil.newSet(asList("Hello", "World", "!").iterator());
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));

            // can be expanded
            set.remove("!");
            set.add("Again");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "Again"))));
        }

        // unmodifiable set
        {
            Set<String> set = CollectionUtil.unmodifiableSetCopy(asList("Hello", "World", "!").iterator());
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));

            // can not be expanded
            try {
                set.remove("!");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                set.add("Again");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
        }
    }

    @Test
    public void shouldCreateLinkedHashSetFromArray() {
        // modifiable set
        {
            LinkedHashSet<String> set = CollectionUtil.newLinkedSet("Hello", "World", "!");
            assertThat(set, equalTo(new LinkedHashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));

            // can be expanded
            set.remove("!");
            set.add("Again");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "Again"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "Again")));
        }

        // unmodifiable set
        {
            Set<String> set = CollectionUtil.unmodifiableLinkedSetCopy("Hello", "World", "!");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));

            // can not be expanded
            try {
                set.remove("!");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                set.add("Again");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));
        }
    }

    @Test
    public void shouldCreateLinkedHashSetFromIterable() {
        Iterable<String> values = asList("Hello", "World", "!");

        // modifiable set
        {
            LinkedHashSet<String> set = CollectionUtil.newLinkedSet(values);
            assertThat(set, equalTo(new LinkedHashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));

            // can be expanded
            set.remove("!");
            set.add("Again");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "Again"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "Again")));
        }

        // unmodifiable set
        {
            Set<String> set = CollectionUtil.unmodifiableLinkedSetCopy(values);
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));

            // can not be expanded
            try {
                set.remove("!");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                set.add("Again");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));
        }
    }

    @Test
    public void shouldCreateLinkedHashSetFromIterator() {
        // modifiable set
        {
            LinkedHashSet<String> set = CollectionUtil.newLinkedSet(asList("Hello", "World", "!").iterator());
            assertThat(set, equalTo(new LinkedHashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));

            // can be expanded
            set.remove("!");
            set.add("Again");
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "Again"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "Again")));
        }

        // unmodifiable set
        {
            Set<String> set = CollectionUtil.unmodifiableLinkedSetCopy(asList("Hello", "World", "!").iterator());
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));

            // can not be expanded
            try {
                set.remove("!");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                set.add("Again");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(set, equalTo(new HashSet<>(asList("Hello", "World", "!"))));
            assertThat(newList(set), equalTo(asList("Hello", "World", "!")));
        }
    }

    @Test
    public void shouldCreateUnmodifiableMapCopy() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("keyA", "valueA");
        values.put("keyB", "valueB");

        // non-linked map
        {
            Map<String, String> map = CollectionUtil.unmodifiableMapCopy(values);
            assertThat(map, equalTo(values));

            try {
                map.remove("keyA");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }

            try {
                map.put("keyC", "valueC");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(map, equalTo(values));
        }
    }

    @Test
    public void shouldCreateUnmodifiableLinkedMapCopy() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("keyA", "valueA");
        values.put("keyB", "valueB");
        values.put("keyC", "valueC");

        // linked map
        {
            Map<String, String> map = CollectionUtil.unmodifiableLinkedMapCopy(values);
            assertThat(map, equalTo(values));
            List<Tuple<String, String>> valuesList = map.entrySet().stream()
                    .map(e -> tuple(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            assertThat(valuesList, equalTo(asList(tuple("keyA", "valueA"), tuple("keyB", "valueB"), tuple("keyC", "valueC"))));

            try {
                map.remove("keyA");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }

            try {
                map.put("keyD", "valueD");
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            assertThat(map, equalTo(values));
            valuesList = map.entrySet().stream()
                    .map(e -> tuple(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            assertThat(valuesList, equalTo(asList(tuple("keyA", "valueA"), tuple("keyB", "valueB"), tuple("keyC", "valueC"))));
        }
    }
}