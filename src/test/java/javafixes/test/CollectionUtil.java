package javafixes.test;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static javafixes.collection.util.CollectionUtil.newList;

public class CollectionUtil {

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
