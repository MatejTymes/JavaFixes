package mtymes.javafixes.object;

import java.util.function.Function;

// todo: javadoc
public abstract class Microtype<T> {

    private final T value;

    protected Microtype(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value of a Microtype can't be null");
        }
        this.value = value;
    }

    public T value() {
        return value;
    }

    public T getValue() {
        return value;
    }

    // todo: test this
    public <T2> T2 map(Function<? super T, ? extends T2> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Microtype mapper can't be null");
        }
        return mapper.apply(value);
    }

    // todo: test this
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Microtype<?> microtype = (Microtype<?>) o;

        return value.equals(microtype.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    // todo: test this
    @Override
    public String toString() {
        return value.toString();
    }
}