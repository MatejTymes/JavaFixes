package javafixes.math;

import java.math.BigInteger;

// todo: extend Number
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
        while (unscaledValue != 0
                && ((int) unscaledValue & 1) == 0
                && unscaledValue % 10 == 0) {
            unscaledValue /= 10;

            if (scale == Integer.MIN_VALUE) {
                throw new ArithmeticException("Scale overflow - can't set scale to less than: " + Integer.MIN_VALUE);
            }
            scale--;
        }

        return new LongDecimal(unscaledValue, scale);
    }

    public static Decimal d(long unscaledValue, int scale) {
        return decimal(unscaledValue, scale);
    }

    public static Decimal decimal(BigInteger unscaledValue, int scale) {
        // todo: strip trailing zeros
        return new HugeDecimal(unscaledValue, scale);
    }

    // todo: test this
    public static Decimal d(BigInteger unscaledValue, int scale) {
        return decimal(unscaledValue, scale);
    }

    abstract public Number unscaledValue();

    abstract public int scale();
}
