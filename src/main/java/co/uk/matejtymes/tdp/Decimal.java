package co.uk.matejtymes.tdp;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.DecimalCalculator.*;
import static co.uk.matejtymes.tdp.LongUtil.*;
import static co.uk.matejtymes.tdp.LongUtil.addExact;
import static co.uk.matejtymes.tdp.LongUtil.multiplyExact;
import static co.uk.matejtymes.tdp.LongUtil.negateExact;
import static co.uk.matejtymes.tdp.LongUtil.subtractExact;
import static co.uk.matejtymes.tdp.RoundUtil.roundBasedOnRemainder;
import static java.lang.Math.*;
import static java.lang.String.format;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;

// todo: check scale on every calculation method
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
        if (scale < 0) {
            throw new IllegalArgumentException(format("Scale (%d) is lower than 0", scale));
        }
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

    public static Decimal decimal(String stringValue) {
        char chars[] = stringValue.toCharArray();
        int startIndex = 0;
        boolean positive = true;
        if (chars[0] == '+') {
            startIndex++;
        } else if (chars[0] == '-') {
            positive = false;
            startIndex++;
        }

        long unscaledValue = 0;
        int scale = 0;

        boolean foundDecimalPoint = false;
        for (int i = startIndex; i < chars.length; i++) {
            char c = chars[i];
            if (c >= '0' && c <= '9') {
//                unscaledValue = unscaledValue * 10 + (c - '0');
                unscaledValue = addExact(multiplyExact(unscaledValue, 10), (c - '0'));
                if (foundDecimalPoint) {
                    ++scale;
                }
            } else if (c == '.') {
                if (foundDecimalPoint) {
                    throw new IllegalArgumentException("Illegal value. Too many decimal points");
                }
                foundDecimalPoint = true;
            } else if (c == '_') {
                // todo: check it is used only between numbers
                // ignore
            } else {
                throw new IllegalArgumentException("Decimal contains invalid character: " + c);
            }
        }
        if (!positive) {
            unscaledValue = negateExact(unscaledValue);
        }

        return new Decimal(unscaledValue, scale);
    }

    // todo: test this
    public static Decimal decimal(BigDecimal value) {
        return decimal(value.toPlainString());
    }

    public long getUnscaledValue() {
        return unscaledValue;
    }

    public int getScale() {
        return scale;
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
        if (this == other) {
            return true;
        }
        return unscaledValue == other.unscaledValue &&
                scale == other.scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Decimal other = (Decimal) o;

        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        // its important that this starts as zero - this way will ignore trailing zeros
        int hashCode = 0;

        long remainder = absExact(unscaledValue);
        while (remainder > 0) {
            hashCode = (hashCode * 5) + (int) (remainder % 10);
            remainder /= 10;
        }
        if (unscaledValue < 0) {
            hashCode *= -1;
        }

        return hashCode;
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
        return multiply(this, value, scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal times(Decimal value, int scaleToUse) {
        return times(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal times(Decimal value) {
        return times(value, min(8, this.scale + value.scale), DEFAULT_ROUNDING_MODE)
                .stripTrailingZerosWithScaleAtLeast(min(this.scale, value.scale));
    }

    // todo: test
    public Decimal div(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        return divide(this, value, scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal div(Decimal value, int scaleToUse) {
        return div(value, scaleToUse, DEFAULT_ROUNDING_MODE);
    }

    // todo: test
    public Decimal div(Decimal value) {
        return div(value, max(8, max(this.scale, value.scale)), DEFAULT_ROUNDING_MODE)
                .stripTrailingZerosWithScaleAtLeast(min(this.scale, value.scale));
    }


    public Decimal rescaleTo(int scaleToUse, RoundingMode roundingMode) {
        if (scaleToUse == scale) {
            return this;
        } else if (scaleToUse > scale) {
            long scaler = pow10(scaleToUse - scale);
            long rescaledValue = multiplyExact(this.unscaledValue, scaler);
            return new Decimal(rescaledValue, scaleToUse);
        }

        long scaler = pow10(scale - scaleToUse);
        long rescaledValue = this.unscaledValue / scaler;
        long remainder = subtractExact(this.unscaledValue, multiplyExact(rescaledValue, scaler));
        long remainingDigit = remainder / (scaler / 10);

        rescaledValue = roundBasedOnRemainder(rescaledValue, remainingDigit, roundingMode);

        return new Decimal(rescaledValue, scaleToUse);
    }

    public Decimal stripTrailingZerosWithScaleAtLeast(int minScaleToKeep) {
        if (minScaleToKeep < 0) {
            throw new IllegalArgumentException(format("Minimal scale to keep (%d), must be at least 0", minScaleToKeep));
        }
        if (scale < minScaleToKeep) {
            return rescaleTo(minScaleToKeep, RoundingMode.UNNECESSARY);
        } else if (scale == minScaleToKeep || unscaledValue % 10 != 0) {
            return this;
        }

        long newUnscaledValue = unscaledValue;
        int newScale = scale;

        while (newUnscaledValue % 10 == 0 && newScale > minScaleToKeep) {
            newUnscaledValue /= 10;
            newScale--;
        }

        return new Decimal(newUnscaledValue, newScale);
    }

    public Decimal stripTrailingZeros() {
        return stripTrailingZerosWithScaleAtLeast(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int charsToDecimalPoint = scale;
        long remainder = unscaledValue;
        do {
            sb.append(abs(remainder % 10));
            if (--charsToDecimalPoint == 0) {
                sb.append('.');
            }
            remainder /= 10;
        } while (remainder != 0 || charsToDecimalPoint >= 0);
        if (unscaledValue < 0) {
            sb.append('-');
        }

        return sb.reverse().toString();
    }

    public static int compare(Decimal x, Decimal y) {
        if (x.scale == y.scale) {
            return Long.compare(x.unscaledValue, y.unscaledValue);
        }

        int minScale = min(x.scale, y.scale);

        long xScaler = pow10(x.scale - minScale);
        long yScaler = pow10(y.scale - minScale);

        // by doing division instead of multiplication we prevent overflow
        long xScaledDownValue = x.unscaledValue / xScaler;
        long yScaledDownValue = y.unscaledValue / yScaler;

        int comparison = Long.compare(xScaledDownValue, yScaledDownValue);
        if (comparison != 0) {
            return comparison;
        } else {
            long xRemainder = subtractExact(x.unscaledValue, multiplyExact(xScaledDownValue, xScaler));
            long yRemainder = subtractExact(y.unscaledValue, multiplyExact(yScaledDownValue, yScaler));

            return Long.compare(xRemainder, yRemainder);
        }
    }
}
