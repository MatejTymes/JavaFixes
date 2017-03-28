package javafixes.math;

public class Scale {

    public static final Scale _0_DECIMAL_PLACES = scale(0);
    public static final Scale _1_DECIMAL_PLACE = scale(1);
    public static final Scale _2_DECIMAL_PLACES = scale(2);
    public static final Scale _3_DECIMAL_PLACES = scale(3);
    public static final Scale _4_DECIMAL_PLACES = scale(4);
    public static final Scale _5_DECIMAL_PLACES = scale(5);
    public static final Scale _8_DECIMAL_PLACES = scale(8);
    public static final Scale _10_DECIMAL_PLACES = scale(10);

    public static final Scale SCALE_OF_TENS = scale(-1); // number after tens will be zero-ed out
    public static final Scale SCALE_OF_HUNDREDS = scale(-2); // number after hundreds will be zero-ed out
    public static final Scale SCALE_OF_THOUSANDS = scale(-3); // number after thousands will be zero-ed out
    public static final Scale SCALE_OF_MILLIONS = scale(-6); // number after millions will be zero-ed out

    public final int value;

    // private constructor so it is non-extendible
    private Scale(int value) {
        this.value = value;
    }

    public static Scale of(int value) {
        return new Scale(value);
    }

    public static Scale scale(int value) {
        return new Scale(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scale scale = (Scale) o;

        return value == scale.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
