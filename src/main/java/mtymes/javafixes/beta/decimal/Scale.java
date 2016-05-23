package mtymes.javafixes.beta.decimal;

public final class Scale {

    public static final Scale Scale_0 = scale(0); // 0 decimal places
    public static final Scale Scale_1 = scale(1); // 1 decimal place
    public static final Scale Scale_2 = scale(2); // 2 decimal places
    public static final Scale Scale_3 = scale(3); // 3 decimal places
    public static final Scale Scale_4 = scale(4); // 4 decimal places
    public static final Scale Scale_5 = scale(5); // 5 decimal places
    public static final Scale Scale_8 = scale(8); // 8 decimal places
    public static final Scale Scale_10 = scale(10); // 10 decimal places

    public static final Scale _0_Decimal_Places = scale(0);
    public static final Scale _1_Decimal_Place = scale(1);
    public static final Scale _2_Decimal_Places = scale(2);
    public static final Scale _3_Decimal_Places = scale(3);
    public static final Scale _4_Decimal_Places = scale(4);
    public static final Scale _5_Decimal_Places = scale(5);
    public static final Scale _8_Decimal_Places = scale(8);
    public static final Scale _10_Decimal_Places = scale(10);

    public static final Scale Scale_Of_Hundreds = scale(-2); // number after hundreds will be zero-ed out
    public static final Scale Scale_Of_Thousands = scale(-3); // number after thousands will be zero-ed out
    public static final Scale Scale_Of_Millions = scale(-6); // number after millions will be zero-ed out



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
