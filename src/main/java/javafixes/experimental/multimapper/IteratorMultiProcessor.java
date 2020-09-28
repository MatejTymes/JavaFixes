package javafixes.experimental.multimapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static javafixes.collection.CollectionUtil.newList;

public class IteratorMultiProcessor {

    public static void main(String[] args) {
        Iterator<String> valuesIterator = newList("This", "are", "the", "3", "out", "of", "5", "books", "older", "than", "10", "years").iterator();

        List<Integer> numbers = newList();
        List<String> nonNumbers = newList();

        processIterator(
                valuesIterator,
                newList(
                        collectTo(value -> isInteger(value), value -> parseInt(value), numbers),
                        collectTo(value -> !isInteger(value), nonNumbers)
                )
        );

        System.out.println(numbers);
        System.out.println(nonNumbers);
    }

    public static <T, O> Consumer<T> collectTo(Function<T, Boolean> filter, Function<T, O> mapper, Collection<O> collection) {
        return value -> {
            if (filter.apply(value)) {
                collection.add(mapper.apply(value));
            }
        };
    }

    public static <T> Consumer<T> collectTo(Function<T, Boolean> filter, Collection<T> collection) {
        return value -> {
            if (filter.apply(value)) {
                collection.add(value);
            }
        };
    }

    public static <T> void processIterator(Iterator<T> values, List<Consumer<T>> valueProcessors) {
        while (values.hasNext()) {
            T value = values.next();
            for (Consumer<T> valueProcessor : valueProcessors) {
                valueProcessor.accept(value);
            }
        }
    }

    private static boolean isInteger(String value) {
        try {
            double d = parseInt(value);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

