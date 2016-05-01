package co.uk.matejtymes.tdp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.DecimalsIntern.*;
import static co.uk.matejtymes.tdp.LongUtil.pow10;
import static java.lang.Math.max;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;

// nice things this class has:
// - fixed equals
// - hash code for values that are comparable is the same
// - sensible rounding defaults
// - string construction can use '_' symbol
// - faster creation than BigDecimal
public abstract class Decimal extends Number implements Comparable<Decimal> {

    public static final Decimal TEN = decimal(10L, 0);
    public static final Decimal ONE = decimal(1L, 0);
    public static final Decimal ZERO = decimal(0, 0);

    private static final RoundingMode DEFAULT_ROUNDING_MODE = HALF_UP;

    // todo: add others like: HugeDecimal, InfiniteDecimal, NaNDecimal
    static final class LongDecimal extends Decimal {

        transient final long unscaledValue;
        transient final int scale;

        LongDecimal(long unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        @Override
        public double doubleValue() {
            if (scale() < 0) {
                return ((double) unscaledValue()) * (double) pow10(-scale());
            } else {
                return ((double) unscaledValue()) / (double) pow10(scale());
            }
        }

        @Override
        long unscaledValue() {
            return unscaledValue;
        }

        @Override
        int scale() {
            return scale;
        }
    }

    // todo: start using this
    static final class HugeDecimal extends Decimal {

        transient final BigInteger unscaledValue;
        transient final int scale;

        HugeDecimal(BigInteger unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        @Override
        long unscaledValue() {
            throw new IllegalStateException("Can't transform into unscaled value of primitive type - the value is too big");
        }

        @Override
        int scale() {
            return scale;
        }

        @Override
        public double doubleValue() {
            throw new IllegalStateException("Can't transform into primitive type - the value is too big");
        }
    }

    private Decimal() {
    }

    // todo: test this
    public static Decimal decimal(int value) {
        return DecimalCreator.createDecimal((long) value, 0);
    }

    // todo: test this
    public static Decimal decimal(long value) {
        return DecimalCreator.createDecimal(value, 0);
    }

    // todo: test this
    public static Decimal decimal(long unscaledValue, int scale) {
        return DecimalCreator.createDecimal(unscaledValue, scale);
    }

    // todo: test this
    public static Decimal decimal(BigInteger unscaledValue, int scale) {
        return DecimalCreator.createDecimal(unscaledValue, scale);
    }

    // todo: test this
    public static Decimal decimal(BigDecimal value) {
        return DecimalParser.parseString(value.toPlainString());
    }

    public static Decimal decimal(String stringValue) {
        return DecimalParser.parseString(stringValue);
    }

    public static Decimal d(String stringValue) {
        return decimal(stringValue);
    }

    @Override
    public int intValue() {
        return (int) doubleValue();
    }

    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    public BigDecimal bigDecimalValue() {
        return new BigDecimal(toString());
    }

    // todo: remove this method
    abstract long unscaledValue();// todo: return this as a Number

    // todo: start handling scale overflow and underflow
    abstract int scale();

    // todo: test this
    public Decimal negate() {
        return DecimalNegator.negate(this);
    }

    // todo: test
    public Decimal plus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return add(this, value, scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal plus(Decimal value, int scaleToUse) {
        return plus(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal plus(Decimal value) {
        return plus(value, max(this.scale(), value.scale()), UNNECESSARY);
    }

    // todo: test
    public Decimal minus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return subtract(this, value, scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal minus(Decimal value, int scaleToUse) {
        return minus(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal minus(Decimal value) {
        return minus(value, max(this.scale(), value.scale()), UNNECESSARY);
    }

    // todo: test
    public Decimal times(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return multiply(this, value, scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal times(Decimal value, int scaleToUse) {
        // todo: add a flag to not be able to use this without a calculator

        return times(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal times(Decimal value) {
        // todo: add a flag to not be able to use this without a calculator

        // todo: define DefaultContext with multiplication settings:
        // todo: Optional<Integer> scaleToUse(int scaleA, int scaleB)
//        return times(value, min(8, this.scale + value.scale), DEFAULT_ROUNDING_MODE);
        return times(value, this.scale() + value.scale(), DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal div(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return divide(this, value, scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal div(Decimal value, int scaleToUse) {
        // todo: add a flag to not be able to use this without a calculator

        return div(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal div(Decimal value) {
        // todo: add a flag to not be able to use this without a calculator

        // todo: define DefaultContext with division settings:
        // todo: Optional<Integer> scaleToUse(int scaleA, int scaleB)
        return div(
                value,
                // todo: change from 8 -> 28 ???
                max(8, max(this.scale(), value.scale())),
                DEFAULT_ROUNDING_MODE
        );
    }


    public Decimal rescaleTo(int scaleToUse, RoundingMode roundingMode) {
        return DecimalsIntern.rescaleTo(this, scaleToUse, roundingMode);
    }

    @Override
    public int compareTo(Decimal other) {
        return compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //  its important to only check its instance of Decimal and not check concrete class\
        //  this will allow us to add subclassing
        if (o == null || !(o instanceof Decimal)) return false;

        return areEqual(this, (Decimal) o);
    }

    @Override
    public int hashCode() {
        return DecimalsIntern.hashCode(this);
    }

    public String toPlainString(int minScaleToUse) {
        return DecimalsIntern.toPlainString(this, minScaleToUse);
    }

    // todo: should we add scientific notation as well ???
    public String toPlainString() {
        return toPlainString(0);
    }

    @Override
    public String toString() {
        return toPlainString();
    }

    public static int compare(Decimal x, Decimal y) {
        return DecimalsIntern.compare(x, y);
    }
}
