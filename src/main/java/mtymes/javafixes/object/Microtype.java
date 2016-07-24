package mtymes.javafixes.object;

import java.util.function.Function;

/**
 * An immutable object that contains a non-null reference to another object.
 * <p>
 * <p>It implements {@code equals()}, {@code hashCode()} and {@code toString()} methods
 * <p>
 * <p>Main purpose of this class is to be able to wrap simple types and add them a class name,
 * to make the code more readable.
 * So instead of using String you could use AccountNumber that extends Microtype&lt;String&gt;
 * or instead of BigDecimal you could use a Salary that extends Microtype&lt;BigDecimal&gt;.
 *
 * @param <T> type of wrapped value
 * @author mtymes
 * @since 07/13/16 09:55 PM
 */
public abstract class Microtype<T> {

    private final T value;

    /**
     * Constructor of {@code Microtype} initialized with value to wrap
     *
     * @param value value that should be wrapped and will be accessible from
     *              created {@code Microtype} instance
     * @throws IllegalArgumentException if {@code null} is passed as input parameter
     */
    protected Microtype(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value of a Microtype can't be null");
        }
        this.value = value;
    }

    /**
     * Returns the wrapped value
     *
     * @return wrapped value
     */
    public T value() {
        return value;
    }

    /**
     * Returns the wrapped value
     *
     * @return wrapped value
     */
    public T getValue() {
        return value();
    }

    /**
     * Maps wrapped value using the provided {@code Function}.
     *
     * @param mapper function to map the wrapped value
     * @param <T2>   the type of generated value
     * @return value generated via the mapping function
     */
    public <T2> T2 map(Function<? super T, ? extends T2> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Microtype mapper can't be null");
        }
        return mapper.apply(value());
    }

    /**
     * Evaluates if object to compare is of the same Microtype type and the wrapped
     * values are equal
     *
     * @param other value to compare to
     * @return {@code true} if other value is of the same Microtype type and wraps
     * the same value. Otherwise returns {@code false}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Microtype<?> microtype = (Microtype<?>) other;

        return value().equals(microtype.value());

    }

    /**
     * Returns wrapped value {@code hashCode()} method response.
     *
     * @return wrapped value hashCode
     */
    @Override
    public int hashCode() {
        return value().hashCode();
    }

    /**
     * Returns wrapped value {@code toString()} method response.
     *
     * @return wrapped value toString
     */
    @Override
    public String toString() {
        return value().toString();
    }
}