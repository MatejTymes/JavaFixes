package mtymes.javafixes.beta.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.max;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;
import static mtymes.javafixes.beta.decimal.OverflowUtil.canCastLongToInt;
import static mtymes.javafixes.beta.decimal.OverflowUtil.didOverflowOnIntAddition;
import static mtymes.javafixes.beta.decimal.PowerMath.numberOfDigits;

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
        public final int signum() {
            return Long.signum(unscaledValue);
        }

        @Override
        public final int scale() {
            return scale;
        }

        @Override
        public final Long unscaledValue() {
            return unscaledValue;
        }

        // todo: test this
        @Override
        public final int precision() {
            return numberOfDigits(unscaledValue);
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
        public final int signum() {
            return unscaledValue.signum();
        }

        @Override
        public final int scale() {
            return scale;
        }

        @Override
        public final BigInteger unscaledValue() {
            return unscaledValue;
        }

        // todo: test this
        @Override
        public final int precision() {
            return numberOfDigits(unscaledValue);
        }
    }

    private Decimal() {
    }

    public static Decimal decimal(long unscaledValue, int scale) {
        // todo: inline this
        return DecimalCreator.createDecimal(unscaledValue, scale);
    }

    public static Decimal decimal(BigInteger unscaledValue, int scale) {
        // todo: inline this
        return DecimalCreator.createDecimal(unscaledValue, scale);
    }

    public static Decimal decimal(int value) {
        return decimal((long) value, 0);
    }

    public static Decimal decimal(long value) {
        return decimal(value, 0);
    }

    public static Decimal decimal(BigInteger unscaledValue) {
        return decimal(unscaledValue, 0);
    }

    public static Decimal decimal(BigDecimal value) {
        return DecimalParser.parseString(value.toPlainString());
    }

    public static Decimal decimal(String stringValue) {
        return DecimalParser.parseString(stringValue);
    }

    // todo: create shorter methods for other combinations as well
    public static Decimal d(String stringValue) {
        return decimal(stringValue);
    }


    public static int compare(Decimal x, Decimal y) {
        return DecimalEqualizer.compare(x, y);
    }


    // todo: add test for this
    abstract public int signum();

    // todo: add test for this
    // todo: start handling scale overflow and underflow
    abstract public int scale();

    // todo: add test for this
    abstract public Number unscaledValue();

    // todo: maybe change this into long
    abstract public int precision();

    // todo: add intValueExact(), longValueExact(), ...

    // todo: test this
    @Override
    public final int intValue() {
        return (int) doubleValue();
    }

    // todo: test this
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

    // todo: test this
    public final Decimal abs() {
        return (signum() < 0) ? negate() : this;
    }

    public final Decimal negate() {
        return DecimalNegator.negate(this);
    }

//    public final Decimal plus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
//        return DecimalAccumulator.add(this, value, scaleToUse, roundingMode);
//    }

//    public final Decimal plus(Decimal value, int scaleToUse) {
//        return plus(value, scaleToUse, DEFAULT_ROUNDING_MODE);
//    }

    public final Decimal plus(Decimal value) {
        return DecimalAccumulator.add(this, value, max(this.scale(), value.scale()), UNNECESSARY);
    }

//    public final Decimal minus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
//        return DecimalAccumulator.subtract(this, value, scaleToUse, roundingMode);
//    }

//    public final Decimal minus(Decimal value, int scaleToUse) {
//        return minus(value, scaleToUse, DEFAULT_ROUNDING_MODE);
//    }

    public final Decimal minus(Decimal value) {
        return DecimalAccumulator.subtract(this, value, max(this.scale(), value.scale()), UNNECESSARY);
    }

    // todo: add Precision based method as well
    public final Decimal times(Decimal value, Scale scaleToUse, RoundingMode roundingMode) {
        return DecimalMultiplier.multiply(this, value, scaleToUse.value, roundingMode);
    }

    // todo: add Precision based method as well
    public final Decimal times(Decimal value, Scale scaleToUse) {
        return DecimalMultiplier.multiply(this, value, scaleToUse.value, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal times(Decimal value) {
        int newScale = this.scale() + value.scale();
        if (didOverflowOnIntAddition(newScale, this.scale(), value.scale())) {
            throw new ArithmeticException("Scale overflow - please provide custom scale value");
        }
        return DecimalMultiplier.multiply(this, value, newScale, UNNECESSARY);
    }

    // todo: add Precision based method as well
    public final Decimal multiply(Decimal value, Scale scaleToUse, RoundingMode roundingMode) {
        return DecimalMultiplier.multiply(this, value, scaleToUse.value, roundingMode);
    }

    // todo: add Precision based method as well
    public final Decimal multiply(Decimal value, Scale scaleToUse) {
        return DecimalMultiplier.multiply(this, value, scaleToUse.value, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal multiply(Decimal value) {
        int newScale = this.scale() + value.scale();
        if (didOverflowOnIntAddition(newScale, this.scale(), value.scale())) {
            throw new ArithmeticException("Scale overflow - please provide custom scale value");
        }
        return DecimalMultiplier.multiply(this, value, newScale, UNNECESSARY);
    }

    // todo: add Precision based method as well
    public final Decimal div(Decimal value, Scale scaleToUse, RoundingMode roundingMode) {
        return DecimalDivider.divide(this, value, scaleToUse.value, roundingMode);
    }

    // todo: add Precision based method as well
    public final Decimal div(Decimal value, Scale scaleToUse) {
        return DecimalDivider.divide(this, value, scaleToUse.value, DEFAULT_ROUNDING_MODE);
    }

    public final Decimal div(Decimal value) {

        // todo: use this algorithm to calculate scaleToUse - and make sure we won't get an int overflow
        // long precisionA = this.precision();
        // long precisionB = value.precision();
        // long precisionToUse = max(28, max(precisionA, precisionB))

        // int scaleA = this.scale()
        // int scaleB = this.scale()
        // long scaleToUse = (long) scaleA - (long) scaleB + precisionToUse - 1

        return DecimalDivider.divide(
                this,
                value,
                max(28, max(this.scale(), value.scale())), // todo: use Precision.PRECISION_34 instead
                DEFAULT_ROUNDING_MODE
        );
    }

    public final Decimal descaleTo(int scaleToUse, RoundingMode roundingMode) {
        return DecimalScaler.descaleTo(this, scaleToUse, roundingMode);
    }

    // todo: test this
    public final Decimal descaleTo(Scale scaleToUse, RoundingMode roundingMode) {
        return DecimalScaler.descaleTo(this, scaleToUse.value, roundingMode);
    }

    public final Decimal descaleTo(int scaleToUse) {
        return descaleTo(scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test this
    public final Decimal descaleTo(Scale scaleToUse) {
        return descaleTo(scaleToUse.value, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public final Decimal deprecisionTo(int precisionToUse, RoundingMode roundingMode) {
        int currentPrecision = precision();
        if (currentPrecision <= precisionToUse) {
            return this;
        }
        long precisionDifference = (long) precisionToUse - currentPrecision;
        long newScale = (long) scale() + precisionDifference;
        if (!canCastLongToInt(newScale)) {
            throw new ArithmeticException("Scale overflow - can't set scale to: " + newScale + ". Please provide a different precision");
        }

        return descaleTo((int) newScale, roundingMode);
    }

    // todo: test
    public final Decimal deprecisionTo(Precision precisionToUse, RoundingMode roundingMode) {
        return deprecisionTo(precisionToUse.value, roundingMode);
    }

    // todo: test
    public final Decimal deprecisionTo(int precisionToUse) {
        return deprecisionTo(precisionToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public final Decimal deprecisionTo(Precision precisionToUse) {
        return deprecisionTo(precisionToUse.value, DEFAULT_ROUNDING_MODE);
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
