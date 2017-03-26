package javafixes.beta.decimal;

// todo: extends Limit or Size
public final class Scale {

    public static final Scale SCALE_0 = scale(0); // 0 decimal places
    public static final Scale SCALE_1 = scale(1); // 1 decimal place
    public static final Scale SCALE_2 = scale(2); // 2 decimal places
    public static final Scale SCALE_3 = scale(3); // 3 decimal places
    public static final Scale SCALE_4 = scale(4); // 4 decimal places
    public static final Scale SCALE_5 = scale(5); // 5 decimal places
    public static final Scale SCALE_8 = scale(8); // 8 decimal places
    public static final Scale SCALE_10 = scale(10); // 10 decimal places

    public static final Scale _0_DECIMAL_PLACES = scale(0);
    public static final Scale _1_DECIMAL_PLACE = scale(1);
    public static final Scale _2_DECIMAL_PLACES = scale(2);
    public static final Scale _3_DECIMAL_PLACES = scale(3);
    public static final Scale _4_DECIMAL_PLACES = scale(4);
    public static final Scale _5_DECIMAL_PLACES = scale(5);
    public static final Scale _8_DECIMAL_PLACES = scale(8);
    public static final Scale _10_DECIMAL_PLACES = scale(10);

    public static final Scale SCALE_OF_HUNDREDS = scale(-2); // number after hundreds will be zero-ed out
    public static final Scale SCALE_OF_THOUSANDS = scale(-3); // number after thousands will be zero-ed out
    public static final Scale SCALE_OF_MILLIONS = scale(-6); // number after millions will be zero-ed out



    public final int value;

    public Scale(int value) {
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
