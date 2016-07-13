package mtymes.javafixes.object;

public abstract class Microtype<T> {

    private final T value;

    protected Microtype(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public T value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Microtype<?> microtype = (Microtype<?>) o;

        return value != null ? value.equals(microtype.value) : microtype.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return (value == null) ? null : value.toString();
    }
}