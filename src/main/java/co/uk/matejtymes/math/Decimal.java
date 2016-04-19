package co.uk.matejtymes.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static co.uk.matejtymes.math.LongUtil.*;
import static co.uk.matejtymes.math.LongUtil.addExact;
import static co.uk.matejtymes.math.LongUtil.multiplyExact;
import static co.uk.matejtymes.math.LongUtil.negateExact;
import static co.uk.matejtymes.math.LongUtil.subtractExact;
import static co.uk.matejtymes.math.RoundUtil.roundBasedOnRemainder;
import static java.lang.Math.*;
import static java.lang.String.format;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;

// todo: add formating
// todo: check scale on every calculation method
// todo: add serialization
public class Decimal extends Number implements Comparable<Decimal> {

    public static final Decimal TEN = new Decimal(10L, 0);
    public static final Decimal ONE = new Decimal(1L, 0);
    public static final Decimal ZERO = new Decimal(0, 0);

    private transient final long unscaledValue;
    private transient final int scale;

    public Decimal(long unscaledValue, int scale) {
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
        int scaleA = getScale();
        int scaleB = value.getScale();
        int maxScale = max(scaleA, scaleB);

        long maxScaledValueA = multiplyExact(unscaledValue, pow10(maxScale - scaleA));
        long maxScaledValueB = multiplyExact(value.unscaledValue, pow10(maxScale - scaleB));

        return new Decimal(
                addExact(maxScaledValueA, maxScaledValueB),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal plus(Decimal value) {
        return plus(value, max(this.scale, value.scale), UNNECESSARY);
    }

    // todo: test
    public Decimal minus(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        int scaleA = getScale();
        int scaleB = value.getScale();
        int maxScale = max(scaleA, scaleB);

        long maxScaledValueA = multiplyExact(unscaledValue, pow10(maxScale - scaleA));
        long maxScaledValueB = multiplyExact(value.unscaledValue, pow10(maxScale - scaleB));

        return new Decimal(
                subtractExact(maxScaledValueA, maxScaledValueB),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal minus(Decimal value) {
        return minus(value, max(this.scale, value.scale), UNNECESSARY);
    }

    // todo: test
    public Decimal times(Decimal value, int scaleToUse, RoundingMode roundingMode) {
        Decimal thisStripped = this.stripTrailingZeros();
        Decimal valueStripped = value.stripTrailingZeros();

        return new Decimal(
                // todo: fix this - this is mostly prone to overflow
                multiplyExact(thisStripped.unscaledValue, valueStripped.unscaledValue),
                thisStripped.scale + valueStripped.scale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    public Decimal times(Decimal value) {
        return times(value, min(8, this.scale + value.scale), HALF_UP)
                .stripTrailingZerosWithScaleAtLeast(min(this.scale, value.scale));
    }

    // todo: test
    public Decimal div(Decimal value, int scaleToUse, RoundingMode roundingMode) {

        boolean positive = (unscaledValue < 0 && value.unscaledValue < 0) ||
                (unscaledValue >= 0 && value.unscaledValue >= 0);

        long remainder = absExact(this.unscaledValue);
        long divisor = absExact(value.unscaledValue);

        long result = remainder / divisor;
        remainder = multiplyExact((remainder % divisor), 10);
        int newScale = this.scale - value.scale;

        if (newScale > scaleToUse) {
            // todo: test this
            return new Decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        } else {
            while (newScale < scaleToUse) {
                result = addExact(multiplyExact(result, 10), (remainder / divisor));
                remainder = multiplyExact((remainder % divisor), 10);
                newScale++;
            }
            long remainingDigit = remainder / divisor;
            if (!positive) {
                result = negateExact(result);
                remainingDigit = -remainingDigit;
            }
            result = roundBasedOnRemainder(result, remainingDigit, roundingMode);


            return new Decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        }
    }

    // todo: test
    public Decimal div(Decimal value) {
        return div(value, max(8, max(this.scale, value.scale)), HALF_UP)
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
