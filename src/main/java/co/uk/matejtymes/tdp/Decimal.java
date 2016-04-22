package co.uk.matejtymes.tdp;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.DecimalCloset.*;
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
public class Decimal extends Number implements Comparable<Decimal> {

    public static final Decimal TEN = decimal(10L, 0);
    public static final Decimal ONE = decimal(1L, 0);
    public static final Decimal ZERO = decimal(0, 0);

    private static final RoundingMode DEFAULT_ROUNDING_MODE = HALF_UP;

    private transient final long unscaledValue;
    private transient final int scale;

    // we should keep the constructor private
    // this people will depend on factory methods and we can actualy introduce more classes
    // like: SmallDecimal, LargeDecimal, InfiniteDecimal, NaNDecimal
    private Decimal(long unscaledValue, int scale) {
        checkScale(scale);

        this.unscaledValue = unscaledValue;
        this.scale = scale;
    }

    // todo: test this
    public static Decimal decimal(int value) {
        return new Decimal((long) value, 0);
    }

    // todo: test this
    public static Decimal decimal(long value) {
        return new Decimal(value, 0);
    }

    // todo: test this
    public static Decimal decimal(long unscaledValue, int scale) {
        return new Decimal(unscaledValue, scale);
    }

    // todo: test this
    public static Decimal decimal(BigDecimal value) {
        return decimal(value.toPlainString());
    }

    public static Decimal decimal(String stringValue) {
        return stringToDecimal(stringValue);
    }

    public static Decimal d(String stringValue) {
        return decimal(stringValue);
    }

    long unscaledValue() {
        return unscaledValue;
    }

    int scale() {
        return scale;
    }

    int scaleWithoutTrailingZeros() {
        // todo: make this more optimized - move into DecimalCloset
        return stripTrailingZeros().scale();
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

    @Override
    public double doubleValue() {
        return ((double) unscaledValue) / (double) pow10(scale);
    }

    public BigDecimal bigDecimalValue() {
        return new BigDecimal(toString());
    }

    @Override
    public int compareTo(Decimal other) {
        return compare(this, other);
    }

    public boolean isIdenticalTo(Decimal other) {
        return areIdentical(this, other);
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
        return DecimalCloset.hashCode(this);
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
        return plus(value, max(this.scale, value.scale), UNNECESSARY);
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
        return minus(value, max(this.scale, value.scale), UNNECESSARY);
    }

    // todo: test
    public Decimal times(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return multiply(this, value, scaleToUse, roundingMode)
                .stripTrailingZeros();
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
        return times(value, this.scale + value.scale, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal div(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return divide(this, value, scaleToUse, roundingMode)
                .stripTrailingZeros();
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
                max(8, max(this.scaleWithoutTrailingZeros(), value.scaleWithoutTrailingZeros())),
                DEFAULT_ROUNDING_MODE
        );
    }


    public Decimal rescaleTo(int scaleToUse, RoundingMode roundingMode) {
        return DecimalCloset.rescaleTo(this, scaleToUse, roundingMode);
    }

    // todo: will anybody ever use this ???
    public Decimal stripTrailingZerosWithScaleAtLeast(int minScaleToKeep) {
        return DecimalCloset.stripTrailingZerosWithScaleAtLeast(this, minScaleToKeep);
    }

    public Decimal stripTrailingZeros() {
        return stripTrailingZerosWithScaleAtLeast(0);
    }

    // todo: should we add scientific notation as well ???
    public String toPlainString() {
        return DecimalCloset.toPlainString(this);
    }

    @Override
    public String toString() {
        return toPlainString();
    }

    public static int compare(Decimal x, Decimal y) {
        return DecimalCloset.compare(x, y);
    }
}
