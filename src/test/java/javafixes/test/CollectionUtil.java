package javafixes.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;

public class CollectionUtil {

    @SafeVarargs
    public static <T> ArrayList<T> newList(T... values) {
        ArrayList<T> list = new ArrayList<>(values.length);
        list.addAll(asList(values));
        return list;
    }

    @SafeVarargs
    public static <T> HashSet<T> newSet(T... values) {
        HashSet<T> set = new HashSet<>(values.length);
        set.addAll(asList(values));
        return set;
    }

    @SafeVarargs
    public static <T, CT extends Collection<T>> CT removeFrom(CT values, T... valuesToRemove) {
        values.removeAll(asList(valuesToRemove));
        return values;
    }

    @SafeVarargs
    public static <T> List<T> removeFrom(T[] values, T... valuesToRemove) {
        return removeFrom(newList(values), valuesToRemove);
    }
}
