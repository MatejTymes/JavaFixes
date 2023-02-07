package javafixes.object;

public interface SmartComparable<T> extends Comparable<T> {

    default boolean isBefore(T other) {
        return this.compareTo(other) < 0;
    }

    default boolean isBeforeOrEqual(T other) {
        return this.compareTo(other) <= 0;
    }

    default boolean isAfter(T other) {
        return this.compareTo(other) > 0;
    }

    default boolean isAfterOrEqual(T other) {
        return this.compareTo(other) >= 0;
    }
}
