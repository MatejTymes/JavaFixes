package javafixes.object;

import javafixes.common.function.TriFunction;

/**
 * Immutable holder of three values. All values can have different types and can be {@code null}.
 * It implements methods {@code equals()}, {@code hashCode()} and {@code toString()}
 * <p>
 * <p>It's useful when if you would like to have a composite return value from a method or as
 * a composite key to a map.
 *
 * @param <A> type of first value
 * @param <B> type of second value
 * @param <C> type of third value
 * @author mtymes
 * @since 07/14/16 8:16 PM
 */
public class Triple<A, B, C> extends DataObject {

    public final A a;
    public final B b;
    public final C c;

    /**
     * Constructor of {@link Triple} with three values to wrap
     *
     * @param a first value
     * @param b second value
     * @param c third value
     */
    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Factory method to create {@link Triple}.
     *
     * @param a   first value
     * @param b   second value
     * @param c   third value
     * @param <A> first value type
     * @param <B> second value type
     * @param <C> third value type
     * @return new {@link Triple}
     */
    public static <A, B, C> Triple<A, B, C> triple(A a, B b, C c) {
        return new Triple<>(a, b, c);
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
     * Returns third value
     *
     * @return third value
     */
    public C getC() {
        return c;
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
     * Returns third value
     *
     * @return third value
     */
    public C c() {
        return c;
    }

    /**
     * Maps wrapped values using a provided {@link TriFunction}
     *
     * @param mapper function to map wrapped values
     * @param <D>    the type of generated value
     * @return value generated via the mapping function
     */
    public <D> D map(TriFunction<? super A, ? super B, ? super C, ? extends D> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Triple mapper can't be null");
        }
        return mapper.apply(a, b, c);
    }
}
