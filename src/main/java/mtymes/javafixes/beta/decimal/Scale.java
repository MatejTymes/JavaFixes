package mtymes.javafixes.beta.decimal;

// todo: start using this
public final class Scale {

    public static final Scale SCALE_0 = scale(0);
    public static final Scale SCALE_2 = scale(2);
    public static final Scale SCALE_3 = scale(3);

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
