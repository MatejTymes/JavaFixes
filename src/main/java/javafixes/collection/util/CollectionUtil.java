package javafixes.collection.util;

import java.util.*;

import static java.util.Collections.*;

// todo: add javadoc
public class CollectionUtil {

    @SafeVarargs
    public static <T> ArrayList<T> newList(T... values) {
        ArrayList<T> list = new ArrayList<T>(values.length);
        Collections.addAll(list, values);
        return list;
    }

    public static <T> ArrayList<T> newList(Iterable<T> values) {
        return (values instanceof Collection)
                ? new ArrayList<T>((Collection<T>) values)
                : newList(values.iterator());
    }

    public static <T> ArrayList<T> newList(Iterator<T> values) {
        ArrayList<T> list = new ArrayList<T>();
        while(values.hasNext()) {
            list.add(values.next());
        }
        return list;
    }

    @SafeVarargs
    public static <T> List<T> unmodifiableListCopy(T... values) {
        return unmodifiableList(newList(values));
    }

    public static <T> List<T> unmodifiableListCopy(Iterable<T> values) {
        return unmodifiableList(newList(values));
    }

    public static <T> List<T> unmodifiableListCopy(Iterator<T> values) {
        return unmodifiableList(newList(values));
    }

    @SafeVarargs
    public static <T> HashSet<T> newSet(T... values) {
        HashSet<T> set = new HashSet<T>(values.length);
        Collections.addAll(set, values);
        return set;
    }

    public static <T> HashSet<T> newSet(Iterable<T> values) {
        return (values instanceof Collection)
                ? new HashSet<T>((Collection<T>) values)
                : newSet(values.iterator());
    }

    public static <T> HashSet<T> newSet(Iterator<T> values) {
        HashSet<T> set = new HashSet<T>();
        while(values.hasNext()) {
            set.add(values.next());
        }
        return set;
    }

    @SafeVarargs
    public static <T> Set<T> unmodifiableSetCopy(T... values) {
        return unmodifiableSet(newSet(values));
    }

    public static <T> Set<T> unmodifiableSetCopy(Iterable<T> values) {
        return unmodifiableSet(newSet(values));
    }

    public static <T> Set<T> unmodifiableSetCopy(Iterator<T> values) {
        return unmodifiableSet(newSet(values));
    }

    @SafeVarargs
    public static <T> LinkedHashSet<T> newLinkedSet(T... values) {
        LinkedHashSet<T> set = new LinkedHashSet<T>(values.length);
        Collections.addAll(set, values);
        return set;
    }

    public static <T> LinkedHashSet<T> newLinkedSet(Iterable<T> values) {
        return (values instanceof Collection)
                ? new LinkedHashSet<T>((Collection<T>) values)
                : newLinkedSet(values.iterator());
    }

    public static <T> LinkedHashSet<T> newLinkedSet(Iterator<T> values) {
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        while(values.hasNext()) {
            set.add(values.next());
        }
        return set;
    }

    @SafeVarargs
    public static <T> Set<T> unmodifiableLinkedSetCopy(T... values) {
        return unmodifiableSet(newLinkedSet(values));
    }

    public static <T> Set<T> unmodifiableLinkedSetCopy(Iterable<T> values) {
        return unmodifiableSet(newLinkedSet(values));
    }

    public static <T> Set<T> unmodifiableLinkedSetCopy(Iterator<T> values) {
        return unmodifiableSet(newLinkedSet(values));
    }

    public static <K, V> Map<K, V> unmodifiableMapCopy(Map<K, V> values) {
        return unmodifiableMap(new HashMap<>(values));
    }

    public static <K, V> Map<K, V> unmodifiableLinkedMapCopy(Map<K, V> values) {
        return unmodifiableMap(new LinkedHashMap<>(values));
    }
}
