package mtymes.javafixes.beta.decimal;

public final class Precision {

    public static final Precision Precision_7 = precision(7);
    public static final Precision _7_Significant_Digits = precision(7);
    public static final Precision Precision_16 = precision(16);
    public static final Precision _16_Significant_Digits = precision(16);
    public static final Precision Precision_34 = precision(34);
    public static final Precision _34_Significant_Digits = precision(34);



    public final int value;

    public Precision(int value) {
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
}
