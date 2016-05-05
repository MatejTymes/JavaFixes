package mtymes.javafixes.beta.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.max;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;

// todo: handle scale overflows and underflow
public abstract class Decimal extends Number implements Comparable<Decimal> {

    public static final Decimal ZERO = decimal(0, 0);
    public static final Decimal ONE = decimal(1L, 0);
    public static final Decimal TEN = decimal(10L, 0);

    private static final RoundingMode DEFAULT_ROUNDING_MODE = HALF_UP;

    static final class LongDecimal extends Decimal {

        transient final long unscaledValue;
        transient final int scale;

        LongDecimal(long unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        // todo: implement faster doubleValue() as in this case it could be much faster

        @Override
        int signum() {
            return Long.signum(unscaledValue);
        }

        @Override
        int scale() {
            return scale;
        }
    }

    static final class HugeDecimal extends Decimal {

        transient final BigInteger unscaledValue;
        transient final int scale;

        HugeDecimal(BigInteger unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        @Override
        int signum() {
            return unscaledValue.signum();
        }

        @Override
        int scale() {
            return scale;
        }
    }

    private Decimal() {
    }

    public static Decimal decimal(int value) {
        return DecimalCreator.createDecimal((long) value, 0);
    }

    public static Decimal decimal(long value) {
        return DecimalCreator.createDecimal(value, 0);
    }

    public static Decimal decimal(long unscaledValue, int scale) {
        return DecimalCreator.createDecimal(unscaledValue, scale);
    }

    public static Decimal decimal(BigInteger unscaledValue) {
        return DecimalCreator.createDecimal(unscaledValue, 0);
    }

    public static Decimal decimal(BigInteger unscaledValue, int scale) {
        return DecimalCreator.createDecimal(unscaledValue, scale);
    }

    public static Decimal decimal(BigDecimal value) {
        return DecimalParser.parseString(value.toPlainString());
    }

    public static Decimal decimal(String stringValue) {
        return DecimalParser.parseString(stringValue);
    }

    public static Decimal d(String stringValue) {
        return decimal(stringValue);
    }


    public static int compare(Decimal x, Decimal y) {
        return DecimalEqualizer.compare(x, y);
    }


    abstract int signum();

    // todo: start handling scale overflow and underflow
    abstract int scale();


    // todo: test this
    // todo: fail if too big
    @Override
    public final int intValue() {
        return (int) doubleValue();
    }

    // todo: test this
    // todo: fail if too big
    @Override
    public final long longValue() {
        return (long) doubleValue();
    }

    // todo: test this
    @Override
    public final float floatValue() {
        return (float) doubleValue();
    }

    // todo: test this
    @Override
    public double doubleValue() {
        // todo: find out which one is faster
//        return Double.parseDouble(toPlainString());
        return Double.parseDouble(toScientificNotation());
    }

    public final BigDecimal bigDecimalValue() {
        return new BigDecimal(toPlainString());
    }

    // todo: add abs
    public final Decimal negate() {
        return DecimalNegator.negate(this);
    }

    public final Decimal plus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return DecimalAccumulator.add(this, value, scaleToUse, roundingMode);
    }

    public final Decimal plus(Decimal value, int scaleToUse) {
        return plus(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal plus(Decimal value) {
        return plus(value, max(this.scale(), value.scale()), UNNECESSARY);
    }

    public final Decimal minus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return DecimalAccumulator.subtract(this, value, scaleToUse, roundingMode);
    }

    public final Decimal minus(Decimal value, int scaleToUse) {
        return minus(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal minus(Decimal value) {
        return minus(value, max(this.scale(), value.scale()), UNNECESSARY);
    }

    public final Decimal times(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return DecimalMultiplier.multiply(this, value, scaleToUse, roundingMode);
    }

    public final Decimal times(Decimal value, int scaleToUse) {
        return times(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal times(Decimal value) {
        return times(value, this.scale() + value.scale(), UNNECESSARY);
    }

    public final Decimal multiply(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return DecimalMultiplier.multiply(this, value, scaleToUse, roundingMode);
    }

    public final Decimal multiply(Decimal value, int scaleToUse) {
        return multiply(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal multiply(Decimal value) {
        return multiply(value, this.scale() + value.scale(), UNNECESSARY);
    }

    public final Decimal div(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return DecimalDivider.divide(this, value, scaleToUse, roundingMode);
    }

    public final Decimal div(Decimal value, int scaleToUse) {
        return div(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal div(Decimal value) {
        return div(
                value,
                // todo: change it to 28 instead ???
                max(18, max(this.scale(), value.scale())),
                DEFAULT_ROUNDING_MODE
        );
    }

    public final Decimal descaleTo(int scaleToUse, RoundingMode roundingMode) {
        return DecimalScaler.descaleTo(this, scaleToUse, roundingMode);
    }

    @Override
    public final int compareTo(Decimal other) {
        return DecimalEqualizer.compare(this, other);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        //  we only check "other" is instance of Decimal and not check concrete class
        //  as this allows us to have subclassing
        if (other == null || !(other instanceof Decimal)) return false;

        return DecimalEqualizer.areEqual(this, (Decimal) other);
    }

    @Override
    public final int hashCode() {
        return DecimalEqualizer.hashCode(this);
    }

    public final String toPlainString(int minScaleToUse) {
        return DecimalPrinter.toPlainString(this, minScaleToUse);
    }

    public final String toPlainString() {
        return toPlainString(0);
    }

    public final String toScientificNotation() {
        return DecimalPrinter.toScientificNotation(this);
    }

    @Override
    public final String toString() {
        return toPlainString();
    }
}
