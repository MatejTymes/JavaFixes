package javafixes.object;

import java.util.function.BiFunction;

/**
 * Immutable holder of two values. Both values can have different type and can be {@code null}.
 * It implements methods {@code equals()}, {@code hashCode()} and {@code toString()}
 * <p>
 * <p>It's useful when if you would like to have a composite return value from a method or as
 * a composite key to a map.
 *
 * @param <A> type of first value
 * @param <B> type of second value
 * @author mtymes
 * @since 07/13/16 8:53 PM
 */
public class Tuple<A, B> extends DataObject {

    public final A a;
    public final B b;

    /**
     * Constructor of {@link Tuple} with two values to wrap
     *
     * @param a first value
     * @param b second value
     */
    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Factory method to create {@link Tuple}.
     *
     * @param a   first value
     * @param b   second value
     * @param <A> first value type
     * @param <B> second value type
     * @return new {@link Tuple}
     */
    public static <A, B> Tuple<A, B> tuple(A a, B b) {
        return new Tuple<>(a, b);
    }

    /**
     * Returns first value
     *
     * @return first value
     */
    public A getA() {
        return a;
    }

    /**
     * Returns second value
     *
     * @return second value
     */
    public B getB() {
        return b;
    }

    /**
     * Returns first value
     *
     * @return first value
     */
    public A a() {
        return a;
    }

    /**
     * Returns second value
     *
     * @return second value
     */
    public B b() {
        return b;
    }

    /**
     * Maps wrapped values using a provided {@link BiFunction}
     *
     * @param mapper function to map wrapped values
     * @param <C>    the type of generated value
     * @return value generated via the mapping function
     */
    public <C> C map(BiFunction<? super A, ? super B, ? extends C> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Tuple mapper can't be null");
        }
        return mapper.apply(a, b);
    }
}
