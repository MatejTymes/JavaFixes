package mtymes.javafixes.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class CollectionUtil {

    public static <T> ArrayList<T> newList(T... values) {
        ArrayList<T> list = new ArrayList<>();
        list.addAll(asList(values));
        return list;
    }

    public static <T, CT extends Collection<T>> CT removeFrom(CT values, T... valuesToRemove) {
        values.removeAll(asList(valuesToRemove));
        return values;
    }

    public static <T> List<T> removeFrom(T[] values, T... valuesToRemove) {
        return removeFrom(newList(values), valuesToRemove);
    }
}
