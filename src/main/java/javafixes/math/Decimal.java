package javafixes.math;

import java.math.BigInteger;

public abstract class Decimal {

    private Decimal() {
    }

    static final class LongDecimal extends Decimal {

        transient final long unscaledValue;
        transient final int scale;

        private LongDecimal(long unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        @Override
        public final Long unscaledValue() {
            return unscaledValue;
        }

        @Override
        public final int scale() {
            return scale;
        }
    }

    static final class HugeDecimal extends Decimal {

        transient final BigInteger unscaledValue;
        transient final int scale;

        private HugeDecimal(BigInteger unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        @Override
        public final BigInteger unscaledValue() {
            return unscaledValue;
        }

        @Override
        public final int scale() {
            return scale;
        }
    }

    public static Decimal decimal(long unscaledValue, int scale) {
        // todo: improve
        return new LongDecimal(unscaledValue, scale);
    }

    public static Decimal d(long unscaledValue, int scale) {
        return decimal(unscaledValue, scale);
    }

    public static Decimal decimal(BigInteger unscaledValue, int scale) {
        // todo: improve
        return new HugeDecimal(unscaledValue, scale);
    }

    // todo: test this
    public static Decimal d(BigInteger unscaledValue, int scale) {
        return decimal(unscaledValue, scale);
    }

    abstract public Number unscaledValue();

    abstract public int scale();
}
