package javafixes.math;

public class Precision {

    public static final Precision _7_SIGNIFICANT_DIGITS = precision(7);
    public static final Precision _16_SIGNIFICANT_DIGITS = precision(16);
    public static final Precision _34_SIGNIFICANT_DIGITS = precision(34);

    public final int value;

    // private constructor so it is non-extendible
    private Precision(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid precision '" + value + "'. Must be greater than zero");
        }
        this.value = value;
    }

    public static Precision of(int value) {
        return new Precision(value);
    }

    public static Precision precision(int value) {
        return new Precision(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Precision precision = (Precision) o;

        return value == precision.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "Precision{" +
                "value=" + value +
                '}';
    }
}
