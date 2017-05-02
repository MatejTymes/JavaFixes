package javafixes.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.math.RoundingMode.*;
import static javafixes.math.BigIntegerUtil.*;
import static javafixes.math.LongUtil.canFitIntoInt;
import static javafixes.math.OverflowUtil.*;
import static javafixes.math.PowerUtil.*;

// todo: unify underflow and overflow exceptions
// todo: add ArithmeticException annotation to all methods that could resolve into it
// todo: add javadoc, formatter and make Serializable
public abstract class Decimal extends Number implements Comparable<Decimal> {

    private static final Precision DEFAULT_PRECISION = Precision._34_SIGNIFICANT_DIGITS;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    public static final Decimal ZERO = decimal(0, 0);
    public static final Decimal ONE = decimal(1, 0);
    public static final Decimal TEN = decimal(1, -1);

    // private constructor so it is non-extendible
    private Decimal() {
    }

    /* =========================== */
    /* ---   implementations   --- */
    /* =========================== */

    private static final class LongDecimal extends Decimal {

        transient final long unscaledValue;
        transient final int scale;

        private LongDecimal(long unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = (unscaledValue == 0) ? 0 : scale;
        }

        @Override
        public final int signum() {
            return Long.signum(unscaledValue);
        }

        @Override
        public final Long unscaledValue() {
            return unscaledValue;
        }

        @Override
        public final int scale() {
            return scale;
        }

        @Override
        public int precision() {
            return numberOfDigits(unscaledValue);
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return BigDecimal.valueOf(unscaledValue, scale);
        }

        @Override
        public Decimal descaleTo(int scaleToUse, RoundingMode roundingMode) {
            if (scaleToUse >= scale) {
                return this;
            }

            return decimal(
                    descaleValue(unscaledValue, scale, scaleToUse, roundingMode),
                    scaleToUse
            );
        }

        @Override
        public Decimal negate() {
            if (unscaledValue != Long.MIN_VALUE) {
                return new LongDecimal(-unscaledValue, scale);
            } else {
                return new HugeDecimal(BigInteger.valueOf(unscaledValue).negate(), scale);
            }
        }

        @Override
        protected boolean isZero() {
            return unscaledValue == 0L;
        }

        @Override
        protected BigInteger unscaledValueAsBigInteger() {
            return BigInteger.valueOf(unscaledValue);
        }
    }

    private static final class HugeDecimal extends Decimal {

        transient final BigInteger unscaledValue;
        transient final int scale;

        private HugeDecimal(BigInteger unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            this.scale = scale;
        }

        @Override
        public final int signum() {
            return unscaledValue.signum();
        }

        @Override
        public final BigInteger unscaledValue() {
            return unscaledValue;
        }

        @Override
        public final int scale() {
            return scale;
        }

        @Override
        public int precision() {
            return numberOfDigits(unscaledValue);
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return new BigDecimal(unscaledValue, scale);
        }

        @Override
        public Decimal descaleTo(int scaleToUse, RoundingMode roundingMode) {
            if (scaleToUse >= scale) {
                return this;
            }

            return decimal(
                    descaleValue(unscaledValue, scale, scaleToUse, roundingMode),
                    scaleToUse
            );
        }

        @Override
        public Decimal negate() {
            return new HugeDecimal(unscaledValue.negate(), scale);
        }

        @Override
        protected boolean isZero() {
            return signum() == 0;
        }

        @Override
        protected BigInteger unscaledValueAsBigInteger() {
            return unscaledValue;
        }
    }

    /* ========================== */
    /* ---   static methods   --- */
    /* ========================== */

    public static Decimal decimal(long unscaledValue, int scale) throws ArithmeticException {
        while (unscaledValue != 0
                && ((int) unscaledValue & 1) == 0
                && unscaledValue % 10 == 0) {
            unscaledValue /= 10;

            if (scale == Integer.MIN_VALUE) {
                throw new ArithmeticException("Scale underflow - can't set scale to less than: " + Integer.MIN_VALUE);
            }
            scale--;
        }

        return new LongDecimal(unscaledValue, scale);
    }

    public static Decimal d(long unscaledValue, int scale) throws ArithmeticException {
        return decimal(unscaledValue, scale);
    }

    public static Decimal decimal(BigInteger unscaledValue, int scale) throws ArithmeticException {
        while (unscaledValue.signum() != 0) {
            BigInteger[] divAndMod = unscaledValue.divideAndRemainder(BigInteger.TEN);
            if (divAndMod[1].signum() != 0) {
                break;
            }
            unscaledValue = divAndMod[0];

            if (scale == Integer.MIN_VALUE) {
                throw new ArithmeticException("Scale underflow - can't set scale to less than: " + Integer.MIN_VALUE);
            }
            scale--;
        }

        if (canConvertToLong(unscaledValue)) {
            long longUnscaledValue = unscaledValue.longValue();
            return new LongDecimal(longUnscaledValue, longUnscaledValue == 0 ? 0 : scale);
        } else {
            return new HugeDecimal(unscaledValue, unscaledValue.signum() == 0 ? 0 : scale);
        }
    }

    public static Decimal d(BigInteger unscaledValue, int scale) throws ArithmeticException {
        return decimal(unscaledValue, scale);
    }

    public static Decimal decimal(int value) {
        return decimal(value, 0);
    }

    public static Decimal d(int value) {
        return decimal(value, 0);
    }

    public static Decimal decimal(long value) {
        return decimal(value, 0);
    }

    public static Decimal d(long value) {
        return decimal(value, 0);
    }

    public static Decimal decimal(BigInteger value) {
        return decimal(value, 0);
    }

    public static Decimal d(BigInteger value) {
        return decimal(value, 0);
    }

    public static Decimal decimal(BigDecimal bigDecimal) {
        return decimal(bigDecimal.unscaledValue(), bigDecimal.scale());
    }

    public static Decimal d(BigDecimal bigDecimal) {
        return decimal(bigDecimal.unscaledValue(), bigDecimal.scale());
    }

    public static Decimal decimal(String stringValue) throws ArithmeticException, NumberFormatException {
        int scale = 0;
        long unscaledValueL = 0;
        BigInteger unscaledValueB = null;

        int signum = 0;
        boolean foundValue = false;
        boolean foundDecimalPoint = false;
        for (int i = 0; i < stringValue.length(); i++) {
            char c = stringValue.charAt(i);
            if (c >= '0' && c <= '9') {
                foundValue = true;
                byte digitToAdd = (byte) (c - '0');
                if (unscaledValueB == null) {
                    if (unscaledValueL <= SAFE_TO_ADD_DIGIT_BOUND) {
                        unscaledValueL = unscaledValueL * 10 + digitToAdd;
                    } else {
                        if (unscaledValueL <= SAFE_TO_MULTIPLY_BY_10_BOUND) {
                            unscaledValueL *= 10;
                            if (unscaledValueL <= Long.MAX_VALUE - digitToAdd) {
                                unscaledValueL += digitToAdd;
                            } else {
                                unscaledValueB = BigInteger.valueOf(unscaledValueL).add(BigInteger.valueOf(digitToAdd));
                            }
                        } else {
                            unscaledValueB = BigInteger.valueOf(unscaledValueL).multiply(BigInteger.TEN).add(BigInteger.valueOf(digitToAdd));
                        }
                    }
                } else {
                    unscaledValueB = unscaledValueB.multiply(BigInteger.TEN).add(BigInteger.valueOf(digitToAdd));
                }
                if (foundDecimalPoint) {
                    // this currently can't happen and is untestable as we can't create char array with so many elements
                    if (scale == Integer.MAX_VALUE) {
                        throw new ArithmeticException("Scale overflow - can't set scale to higher value than than: " + Integer.MAX_VALUE);
                    }
                    ++scale;
                }
            } else if (c == '.') {
                if (foundDecimalPoint) {
                    throw new NumberFormatException("Illegal value. Too many decimal points");
                }
                foundDecimalPoint = true;
            } else if (c == '_') {
                // ignore
            } else if (c == '-') {
                if (foundValue || foundDecimalPoint || signum != 0) {
                    throw new NumberFormatException("Illegal value. Unexpected sign symbol '" + c + "'");
                }
                signum = -1;
            } else if (c == '+') {
                if (foundValue || foundDecimalPoint || signum != 0) {
                    throw new NumberFormatException("Illegal value. Unexpected sign symbol '" + c + "'");
                }
                signum = 1;
            } else if (c == 'e' || c == 'E') {
                i++;
                boolean foundExponentValue = false;
                int exponentSignum = 0;
                long exponent = 0;
                for (; i < stringValue.length(); i++) {
                    c = stringValue.charAt(i);
                    if (c >= '0' && c <= '9') {
                        foundExponentValue = true;
                        byte digitToAdd = (byte) (c - '0');
                        exponent = exponent * 10 + digitToAdd;
                        if (exponent > DOABLE_SCALE) {
                            throw new ArithmeticException("Illegal value. Scale won't fit into Integer");
                        }
                    } else if (c == '-') {
                        if (foundExponentValue || exponentSignum != 0) {
                            throw new NumberFormatException("Illegal value. Unexpected sign symbol '" + c + "'");
                        }
                        exponentSignum = -1;
                    } else if (c == '+') {
                        if (foundExponentValue || exponentSignum != 0) {
                            throw new NumberFormatException("Illegal value. Unexpected sign symbol '" + c + "'");
                        }
                        exponentSignum = 1;
                    } else if (c == '_') {
                        // ignore
                    } else {
                        throw new NumberFormatException("Illegal value. Invalid character '" + c + "'");
                    }
                }

                exponent = (exponentSignum != -1) ? exponent : -exponent;
                exponent = (long) scale - exponent;
                if (!LongUtil.canFitIntoInt(exponent)) {
                    throw new ArithmeticException("Illegal value. Scale '" + exponent + "' won't fit into Integer");
                }
                scale = (int) exponent;
            } else {
                throw new NumberFormatException("Illegal value. Invalid character '" + c + "'");
            }
        }

        if (!foundValue) {
            throw new NumberFormatException("Illegal value. No numeric value defined");
        }

        return unscaledValueB == null
                ? decimal(signum == -1 ? -unscaledValueL : unscaledValueL, scale)
                : decimal(signum == -1 ? unscaledValueB.negate() : unscaledValueB, scale);
    }

    public static Decimal d(String stringValue) {
        return decimal(stringValue);
    }

    /* ======================= */
    /* ---   api methods   --- */
    /* ======================= */

    abstract public int signum();

    abstract public Number unscaledValue();

    abstract public int scale();

    abstract public int precision();

    // todo: maybe reuse the toString logic
    abstract public BigDecimal bigDecimalValue();

    // todo: add int/long/float/double Exact()

    @Override
    public int intValue() {
        int scale = scale();
        if (scale == 0) {
            return unscaledValue().intValue();
        }

        if (unscaledValue() instanceof Long) {
            long unscaledValue = (Long) unscaledValue();
            if (scale > 0) {
                return (int) descaleValue(unscaledValue, scale, 0, DOWN);
            } else {
                if (canUpscaleLongByPowerOf10(unscaledValue, -scale)) {
                    return (int) upscaleByPowerOf10(unscaledValue, -scale);
                }
            }
        }

        BigInteger unscaledValue = unscaledValueAsBigInteger();
        if (scale > 0) {
            return descaleValue(unscaledValue, scale, 0, DOWN).intValue();
        } else {
            return upscaleByPowerOf10(unscaledValue, -scale).intValue();
        }
    }

    @Override
    public long longValue() {
        int scale = scale();
        if (scale == 0) {
            return unscaledValue().longValue();
        }

        if (unscaledValue() instanceof Long) {
            long unscaledValue = (Long) unscaledValue();
            if (scale > 0) {
                return descaleValue(unscaledValue, scale, 0, DOWN);
            } else {
                if (canUpscaleLongByPowerOf10(unscaledValue, -scale)) {
                    return upscaleByPowerOf10(unscaledValue, -scale);
                }
            }
        }

        BigInteger unscaledValue = unscaledValueAsBigInteger();
        if (scale > 0) {
            return descaleValue(unscaledValue, scale, 0, DOWN).longValue();
        } else {
            return upscaleByPowerOf10(unscaledValue, -scale).longValue();
        }
    }

    @Override
    public float floatValue() {
        // todo: add faster implementation, but good for now
        return Float.parseFloat(toScientificNotation());
    }

    @Override
    public double doubleValue() {
        // todo: add faster implementation, but good for now
        return Double.parseDouble(toScientificNotation());
    }

    abstract public Decimal descaleTo(int scaleToUse, RoundingMode roundingMode);

    public Decimal descaleTo(Scale scaleToUse, RoundingMode roundingMode) {
        return descaleTo(scaleToUse.value, roundingMode);
    }

    public Decimal descaleTo(int scaleToUse) {
        return descaleTo(scaleToUse, DEFAULT_ROUNDING);
    }

    public Decimal descaleTo(Scale scaleToUse) {
        return descaleTo(scaleToUse.value, DEFAULT_ROUNDING);
    }

    public Decimal deprecisionTo(int precisionToUse, RoundingMode roundingMode) {
        if (precisionToUse <= 0) {
            throw new IllegalArgumentException(format("Invalid precision '%d'. Should be greater than 0.", precisionToUse));
        }

        int precision = precision();
        if (precisionToUse >= precision) {
            return this;
        }

        long scaleToUse = ((long) scale()) - ((long) precision - (long) precisionToUse);
        if (!canFitIntoInt(scaleToUse)) {
            throw new ArithmeticException(format("Scale overflow - can't set precision to %d as it would resolve into non-integer scale %d", precisionToUse, scaleToUse));
        }

        return descaleTo((int) scaleToUse, roundingMode);
    }

    public Decimal deprecisionTo(Precision precisionToUse, RoundingMode roundingMode) {
        return deprecisionTo(precisionToUse.value, roundingMode);
    }

    public Decimal deprecisionTo(int precisionToUse) {
        return deprecisionTo(precisionToUse, DEFAULT_ROUNDING);
    }

    public Decimal deprecisionTo(Precision precisionToUse) {
        return deprecisionTo(precisionToUse.value, DEFAULT_ROUNDING);
    }

    abstract public Decimal negate();

    public Decimal abs() {
        return (signum() < 0) ? negate() : this;
    }

    public Decimal plus(Decimal value) {
        if (this.isZero()) {
            return value;
        } else if (value.isZero()) {
            return this;
        } else if (this instanceof LongDecimal && value instanceof LongDecimal) {
            return sumOf(
                    ((LongDecimal) this).unscaledValue,
                    ((LongDecimal) value).unscaledValue,
                    ((LongDecimal) this).scale,
                    ((LongDecimal) value).scale
            );
        } else {
            return sumOf(
                    this.unscaledValueAsBigInteger(),
                    value.unscaledValueAsBigInteger(),
                    this.scale(),
                    value.scale()
            );
        }
    }

    public Decimal minus(Decimal value) {
        if (this.isZero()) {
            return value.negate();
        } else if (value.isZero()) {
            return this;
        } else if (this instanceof LongDecimal && value instanceof LongDecimal) {
            long unscaledValueB = ((LongDecimal) value).unscaledValue;
            if (willNegationOverflow(unscaledValueB)) {
                return sumOf(
                        this.unscaledValueAsBigInteger(),
                        BigInteger.valueOf(unscaledValueB).negate(),
                        ((LongDecimal) this).scale,
                        ((LongDecimal) value).scale
                );
            } else {
                return sumOf(
                        ((LongDecimal) this).unscaledValue,
                        -unscaledValueB,
                        ((LongDecimal) this).scale,
                        ((LongDecimal) value).scale
                );
            }
        } else {
            return sumOf(
                    this.unscaledValueAsBigInteger(),
                    value.unscaledValueAsBigInteger().negate(),
                    this.scale(),
                    value.scale()
            );
        }
    }

    public Decimal times(Decimal value) {
        if (this.isZero() || value.isZero()) {
            return ZERO;
        } else if (this instanceof LongDecimal && value instanceof LongDecimal) {
            return multiply(
                    ((LongDecimal) this).unscaledValue,
                    ((LongDecimal) value).unscaledValue,
                    (long) ((LongDecimal) this).scale + (long) ((LongDecimal) value).scale
            );
        } else {
            return multiply(
                    this.unscaledValueAsBigInteger(),
                    value.unscaledValueAsBigInteger(),
                    (long) this.scale() + (long) value.scale()
            );
        }
    }

    public Decimal div(Decimal value, Precision precisionToUse, RoundingMode roundingMode) {
        if (value.isZero()) {
            throw new ArithmeticException("Division by zero not allowed");
        } else if (this.isZero()) {
            return ZERO;
        }

        long powerIncrease = max(0L, (long) precisionToUse.value - this.precision() + value.precision() + 1);
        if (!canFitIntoInt(powerIncrease)) {
            throw new ArithmeticException("Integer Overflow while dividing decimals");
        }

        BigInteger poweredUpValueA = this.unscaledValueAsBigInteger();
        if (powerIncrease > 0L) {
            poweredUpValueA = poweredUpValueA.multiply(powerOf10Big((int) powerIncrease));
        }

        BigInteger[] divAndRemainder = poweredUpValueA.divideAndRemainder(value.unscaledValueAsBigInteger());
        BigInteger result = divAndRemainder[0];
        boolean hasAdditionalRemainder = (divAndRemainder[1].signum() != 0);
        int currentPrecision = numberOfDigits(result);

        long longScale = (long) this.scale() - value.scale() + powerIncrease;
        if (currentPrecision - 1 > precisionToUse.value) {
            int precisionDiff = currentPrecision - 1 - precisionToUse.value;
            divAndRemainder = result.divideAndRemainder(powerOf10Big(precisionDiff));
            result = divAndRemainder[0];
            hasAdditionalRemainder |= (divAndRemainder[1].signum() != 0);
            longScale -= precisionDiff;
        }

        if (!canFitIntoInt(longScale - 1)) {
            if (longScale > Integer.MAX_VALUE) {
                throw new ArithmeticException(format("Scale overflow - can't set precision to %d as it would resolve into non-integer scale %d", precisionToUse.value, longScale));
            } else {
                throw new ArithmeticException(format("Scale underflow - can't set precision to %d as it would resolve into non-integer scale %d", precisionToUse.value, longScale));
            }
        }

        divAndRemainder = result.divideAndRemainder(BigInteger.TEN);
        int scale = (int) (longScale - 1);
        result = divAndRemainder[0];
        int roundingDigit = divAndRemainder[1].intValue();

        int resultSign = this.signum() * value.signum();
        int correction = roundingCorrection(resultSign, result, Math.abs(roundingDigit), hasAdditionalRemainder, roundingMode);
        BigInteger roundingCorrection = (correction == 1) ? BIG_INTEGER_ONE : (correction == -1) ? BIG_INTEGER_MINUS_ONE : BigInteger.ZERO;
        result = result.add(roundingCorrection);

        return decimal(result, scale);
    }

    public Decimal div(Decimal value, Scale scaleToUse, RoundingMode roundingMode) {
        if (value.isZero()) {
            throw new ArithmeticException("Division by zero not allowed");
        } else if (this.isZero()) {
            return ZERO;
        }

        long powerIncrease = max(0L, scaleToUse.value + value.scale() - this.scale() + 1);
        if (!canFitIntoInt(powerIncrease)) {
            throw new ArithmeticException("Integer Overflow while dividing decimals");
        }

        BigInteger poweredUpValueA = this.unscaledValueAsBigInteger();
        if (powerIncrease > 0L) {
            poweredUpValueA = poweredUpValueA.multiply(powerOf10Big((int) powerIncrease));
        }

        BigInteger[] divAndRemainder = poweredUpValueA.divideAndRemainder(value.unscaledValueAsBigInteger());
        BigInteger result = divAndRemainder[0];
        boolean hasAdditionalRemainder = (divAndRemainder[1].signum() != 0);

        long longScale = (long) this.scale() - value.scale() + powerIncrease;
        if (longScale - 1 > scaleToUse.value) {
            long scaleDiff = longScale - 1 - scaleToUse.value;
            if (!canFitIntoInt(scaleDiff)) {
                throw new ArithmeticException("Integer Overflow while dividing decimals");
            }
            divAndRemainder = result.divideAndRemainder(powerOf10Big((int) scaleDiff));
            result = divAndRemainder[0];
            hasAdditionalRemainder |= (divAndRemainder[1].signum() != 0);
            longScale -= scaleDiff;
        }

        divAndRemainder = result.divideAndRemainder(BigInteger.TEN);
        int scale = (int) (longScale - 1);
        result = divAndRemainder[0];
        int roundingDigit = divAndRemainder[1].intValue();

        int resultSign = this.signum() * value.signum();
        int correction = roundingCorrection(resultSign, result, Math.abs(roundingDigit), hasAdditionalRemainder, roundingMode);
        BigInteger roundingCorrection = (correction == 1) ? BIG_INTEGER_ONE : (correction == -1) ? BIG_INTEGER_MINUS_ONE : BigInteger.ZERO;
        result = result.add(roundingCorrection);

        return decimal(result, scale);
    }

    public Decimal div(Decimal value, Precision precisionToUse) {
        return div(value, precisionToUse, DEFAULT_ROUNDING);
    }

    public Decimal div(Decimal value, Scale scaleToUse) {
        return div(value, scaleToUse, DEFAULT_ROUNDING);
    }

    public Decimal div(Decimal value) {
        return div(value, DEFAULT_PRECISION, DEFAULT_ROUNDING);
    }

    public Decimal pow(int n) {
        if (n < 0) {
            throw new ArithmeticException("Can't calculate power using negative exponent " + n);
        } else if (n == 0) {
            return ONE;
        } else if (n == 1) {
            return this;
        }

        long resultScale = (long) scale() * (long) n;
        if (!canFitIntoInt(resultScale)) {
            if (resultScale > Integer.MAX_VALUE) {
                throw new ArithmeticException(format("Scale overflow - can't calculate power of %d as it would resolve into non-integer scale %d", n, resultScale));
            } else {
                throw new ArithmeticException(format("Scale underflow - can't calculate power of %d as it would resolve into non-integer scale %d", n, resultScale));
            }
        }

        return decimal(
                unscaledValueAsBigInteger().pow(n),
                (int) resultScale
        );
    }

    @Override
    public int compareTo(Decimal other) {
        return compare(this, other);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        //  we only check "other" is instance of Decimal and not check concrete class
        //  as this allows us to have subclassing
        if (other == null || !(other instanceof Decimal)) return false;

        return this.compareTo((Decimal) other) == 0;
    }

    @Override
    public int hashCode() {
        // its important that this starts as zero - this way we'll ignore trailing zeros
        int hashCode = 0;

        if (this instanceof LongDecimal) {
            long remainder = ((LongDecimal) this).unscaledValue;
            while (remainder != 0) {
                hashCode = (hashCode * 5) + (int) (remainder % 10);
                remainder /= 10;
            }
        } else if (this instanceof HugeDecimal) {
            BigInteger remainder = ((HugeDecimal) this).unscaledValue;
            while (remainder.signum() != 0) {
                BigInteger[] divAndMod = remainder.divideAndRemainder(BigInteger.TEN);
                remainder = divAndMod[0];
                hashCode = (hashCode * 5) + divAndMod[1].intValue();
            }
        } else {
            throw new UnsupportedDecimalTypeException(this);
        }

        return hashCode;
    }

    @Override
    public String toString() {
        int scale = scale();
        if (scale <= -9 || (scale > 9 && scale - precision() >= 9)) {
            return toScientificNotation();
        } else {
            return toPlainString();
        }
    }

    public String toPlainString() {
        return toPlainString(1);
    }

    // scale lower than 0 will be ignored and 0 will be used instead
    public String toPlainString(int minScaleToUse) {
        StringBuilder sb = new StringBuilder(unscaledValue().toString());

        int currentScale = scale();
        int scaleToUse = max(currentScale, max(minScaleToUse, 0));
        if (currentScale < scaleToUse) {
            sb.append(arrayOfZeroChars((long) scaleToUse - currentScale));
        }

        if (scaleToUse > 0) {
            boolean isNegative = signum() < 0;
            int prefixZerosOffset = isNegative ? 1 : 0;
            int firstDigitScale = scaleToUse - (sb.length() - prefixZerosOffset) + 1;
            if (firstDigitScale > 0) {
                sb.insert(prefixZerosOffset, arrayOfZeroChars(firstDigitScale));
            }
            int index = sb.length() - scaleToUse;
            sb.insert(index, '.');
        }

        return sb.toString();
    }

    public String toScientificNotation() {
        StringBuilder sb = new StringBuilder(unscaledValue().toString());

        int signOffset = (signum() < 0) ? 1 : 0;
        long exponent = ((long) sb.length() - signOffset) - scale() - 1;

        if (sb.length() > 1 + signOffset) {
            sb.insert(1 + signOffset, '.');
        }
        sb.append("e").append(exponent);

        return sb.toString();
    }

    /* ==================================== */
    /* ---   protected helper methods   --- */
    /* ==================================== */

    protected abstract boolean isZero();

    protected abstract BigInteger unscaledValueAsBigInteger();

    /* ============================ */
    /* ---   groovy operators   --- */
    /* ============================ */

    Decimal multiply(Decimal value) {
        return times(value);
    }

    /* ================================== */
    /* ---   private static methods   --- */
    /* ================================== */

    private static int compare(Decimal a, Decimal b) {

        int signumA = a.signum();
        int signumB = b.signum();
        int signComparison = Integer.compare(signumA, signumB);
        if (signComparison != 0) {
            return signComparison;
        }

        int scaleA = a.scale();
        int scaleB = b.scale();

        long highestDigitPositionA = ((long) a.precision()) - ((long) scaleA);
        long highestDigitPositionB = ((long) b.precision()) - ((long) scaleB);
        if (highestDigitPositionA > highestDigitPositionB) {
            return (signumA >= 0) ? 1 : -1;
        } else if (highestDigitPositionA < highestDigitPositionB) {
            return (signumA >= 0) ? -1 : 1;
        }

        if (a instanceof LongDecimal && b instanceof LongDecimal) {
            long unscaledA = ((LongDecimal) a).unscaledValue;
            long unscaledB = ((LongDecimal) b).unscaledValue;

            if (scaleA == scaleB) {
                return Long.compare(unscaledA, unscaledB);
            }

            int scaleToGet = min(scaleA, scaleB);

            // upper part comparison
            long rescaledA = descaleValue(unscaledA, scaleA, scaleToGet);
            long rescaledB = descaleValue(unscaledB, scaleB, scaleToGet);

            int topComparison = Long.compare(rescaledA, rescaledB);
            if (topComparison != 0) {
                return topComparison;
            } else {
                // remainder comparison
                long remainderA = unscaledA - upScaleValue(rescaledA, scaleToGet, scaleA);
                long remainderB = unscaledB - upScaleValue(rescaledB, scaleToGet, scaleB);

                return Long.compare(remainderA, remainderB);
            }
        } else {
            // todo: might be slow for huge scale differences
            BigInteger unscaledA = bigUnscaledValueFrom(a);
            BigInteger unscaledB = bigUnscaledValueFrom(b);

            int maxScale = max(scaleA, scaleB);

            if (scaleA < maxScale) {
                unscaledA = upscaleByPowerOf10(unscaledA, (long) maxScale - scaleA);
            }

            if (scaleB < maxScale) {
                unscaledB = upscaleByPowerOf10(unscaledB, (long) maxScale - scaleB);
            }

            return unscaledA.compareTo(unscaledB);
        }
    }

    private static BigInteger bigUnscaledValueFrom(Decimal d) {
        BigInteger unscaledValue;
        if (d instanceof LongDecimal) {
            unscaledValue = BigInteger.valueOf(((LongDecimal) d).unscaledValue);
        } else if (d instanceof HugeDecimal) {
            unscaledValue = ((HugeDecimal) d).unscaledValue;
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
        return unscaledValue;
    }

    private static long descaleValue(long value, int fromScale, int toScale) {
        return (toScale < fromScale)
                ? downscaleByPowerOf10(value, (long) fromScale - toScale)
                : value;
    }

    private static long upScaleValue(long value, int fromScale, int toScale) {
        return (toScale > fromScale)
                ? upscaleByPowerOf10(value, (long) toScale - fromScale)
                : value;
    }

    private static long descaleValue(long unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        long scaleDiff = (long) scale - scaleToUse;

        long valueWithRoundingDigit = downscaleByPowerOf10(unscaledValue, scaleDiff - 1);
        boolean hasAdditionalRemainder = unscaledValue != upscaleByPowerOf10(valueWithRoundingDigit, scaleDiff - 1);
        if (valueWithRoundingDigit == 0 && !hasAdditionalRemainder) {
            return 0;
        }

        long rescaledValue = valueWithRoundingDigit / 10L;
        int remainingDigit = (int) (valueWithRoundingDigit - (rescaledValue * 10L));

        int roundingCorrection = roundingCorrection(
                Long.signum(unscaledValue),
                rescaledValue,
                remainingDigit,
                hasAdditionalRemainder,
                roundingMode
        );
        rescaledValue += roundingCorrection;

        return rescaledValue;
    }

    private static BigInteger descaleValue(BigInteger unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        long scaleDiff = (long) scale - (long) scaleToUse;

        boolean hasAdditionalRemainder = false;
        BigInteger valueWithRoundingDigit = unscaledValue;
        long n = scaleDiff - 1;
        while (n > 0 && valueWithRoundingDigit.signum() != 0) {
            int descaleBy = (int) Math.min((long) maxCachedBigPowerOf10(), n);
            BigInteger[] divAndMod = valueWithRoundingDigit.divideAndRemainder(powerOf10Big(descaleBy));
            valueWithRoundingDigit = divAndMod[0];
            if (divAndMod[1].signum() != 0) {
                hasAdditionalRemainder = true;
            }
            n -= descaleBy;
        }

        if (valueWithRoundingDigit.signum() == 0 && !hasAdditionalRemainder) {
            return BigInteger.ZERO;
        }

        BigInteger[] divAndMod = valueWithRoundingDigit.divideAndRemainder(BigInteger.TEN);
        BigInteger rescaledValue = divAndMod[0];
        int remainingDigit = divAndMod[1].intValue();
        if (unscaledValue.signum() < 0 && remainingDigit > 0) {
            remainingDigit -= 10;
        }

        int roundingCorrection = roundingCorrection(
                unscaledValue.signum(),
                rescaledValue,
                remainingDigit,
                hasAdditionalRemainder,
                roundingMode
        );
        if (roundingCorrection == 1) {
            rescaledValue = rescaledValue.add(BigInteger.ONE);
        } else if (roundingCorrection == -1) {
            rescaledValue = rescaledValue.subtract(BigInteger.ONE);
        }

        return rescaledValue;
    }

    private static char[] arrayOfZeroChars(long size) {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalStateException("unable to create String with " + size + " characters");
        }
        char[] zeros = new char[(int) size];
        Arrays.fill(zeros, '0');
        return zeros;
    }

    private static int roundingCorrection(int signum, long valueToRound, int roundingDigit, boolean hasAdditionalRemainder, RoundingMode roundingMode) {
        Boolean isDigitToRoundOdd = null;
        if (roundingMode == HALF_EVEN) {
            isDigitToRoundOdd = ((int) valueToRound & 1) == 1;
        }
        return roundingCorrection(signum, isDigitToRoundOdd, Math.abs(roundingDigit), hasAdditionalRemainder, roundingMode);
    }

    private static int roundingCorrection(int signum, BigInteger valueToRound, int roundingDigit, boolean hasAdditionalRemainder, RoundingMode roundingMode) {
        Boolean isDigitToRoundOdd = null;
        if (roundingMode == HALF_EVEN) {
            isDigitToRoundOdd = (valueToRound.intValue() & 1) == 1;
        }
        return roundingCorrection(signum, isDigitToRoundOdd, Math.abs(roundingDigit), hasAdditionalRemainder, roundingMode);
    }

    private static int roundingCorrection(int signum, Boolean isDigitToRoundOdd, int roundingDigit, boolean hasAdditionalRemainder, RoundingMode roundingMode) {
        if (signum < -1 || signum > 1) {
            throw new IllegalArgumentException(format("Invalid signum (%d). Should be between -1 and 1", signum));
        } else if (roundingDigit < 0 || roundingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid rounding digit (%d). Should be between 0 and 9", roundingDigit));
        }

        int roundingCorrection = 0;

        if (roundingDigit != 0 || hasAdditionalRemainder) {
            if (roundingMode == HALF_UP) {
                if (roundingDigit >= 5) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == UP) {
                if (roundingDigit > 0 || hasAdditionalRemainder) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == DOWN) {
                // do nothing
            } else if (roundingMode == CEILING) {
                if ((roundingDigit > 0 || hasAdditionalRemainder) && signum >= 0) {
                    roundingCorrection = 1;
                }
            } else if (roundingMode == FLOOR) {
                if ((roundingDigit > 0 || hasAdditionalRemainder) && signum == -1) {
                    roundingCorrection = -1;
                }
            } else if (roundingMode == HALF_DOWN) {
                if (roundingDigit > 5 || (roundingDigit == 5 && hasAdditionalRemainder)) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == HALF_EVEN) {
                if (roundingDigit > 5 || (roundingDigit == 5 && (hasAdditionalRemainder || isDigitToRoundOdd))) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == UNNECESSARY) {
                throw new IllegalArgumentException("Rounding necessary");
            }
        }

        return roundingCorrection;
    }

    private static Decimal sumOf(long unscaledValueA, long unscaledValueB, int scaleA, int scaleB) {
        if (scaleA == scaleB) {
            return sumOf(unscaledValueA, unscaledValueB, scaleA);
        } else {
            long scaleDiff = (long) scaleA - (long) scaleB;
            if (scaleDiff < 0) {
                if (canUpscaleLongByPowerOf10(unscaledValueA, -scaleDiff)) {
                    return sumOf(
                            upscaleByPowerOf10(unscaledValueA, -scaleDiff),
                            unscaledValueB,
                            scaleB
                    );
                } else {
                    return sumOf(
                            upscaleByPowerOf10(BigInteger.valueOf(unscaledValueA), -scaleDiff),
                            BigInteger.valueOf(unscaledValueB),
                            scaleB
                    );
                }
            } else {
                if (canUpscaleLongByPowerOf10(unscaledValueB, scaleDiff)) {
                    return sumOf(
                            unscaledValueA,
                            upscaleByPowerOf10(unscaledValueB, scaleDiff),
                            scaleA
                    );
                } else {
                    return sumOf(
                            BigInteger.valueOf(unscaledValueA),
                            upscaleByPowerOf10(BigInteger.valueOf(unscaledValueB), scaleDiff),
                            scaleA
                    );
                }
            }
        }
    }

    private static Decimal sumOf(BigInteger unscaledValueA, BigInteger unscaledValueB, int scaleA, int scaleB) {
        if (scaleA == scaleB) {
            return sumOf(
                    unscaledValueA,
                    unscaledValueB,
                    scaleA
            );
        } else {
            long scaleDiff = (long) scaleA - (long) scaleB;
            if (scaleDiff < 0) {
                return sumOf(
                        upscaleByPowerOf10(unscaledValueA, -scaleDiff),
                        unscaledValueB,
                        scaleB
                );
            } else {
                return sumOf(
                        unscaledValueA,
                        upscaleByPowerOf10(unscaledValueB, scaleDiff),
                        scaleA
                );
            }
        }
    }

    private static Decimal sumOf(long valueA, long valueB, int scale) {
        long sum = valueA + valueB;
        if (didOverflowOnLongAddition(sum, valueA, valueB)) {
            return sumOf(BigInteger.valueOf(valueA), BigInteger.valueOf(valueB), scale);
        }
        return decimal(sum, scale);
    }

    private static Decimal sumOf(BigInteger valueA, BigInteger valueB, int scale) {
        return decimal(valueA.add(valueB), scale);
    }

    private static Decimal multiply(long valueA, long valueB, long scale) {
        if (!canFitIntoInt(scale)) {
            throw new ArithmeticException("Illegal result. Scale '" + scale + "' won't fit into Integer");
        }

        long result = valueA * valueB;
        if (didOverflowOnMultiplication(result, valueA, valueB)) {
            return multiply(BigInteger.valueOf(valueA), BigInteger.valueOf(valueB), scale);
        }
        return decimal(result, (int) scale);
    }

    private static Decimal multiply(BigInteger valueA, BigInteger valueB, long scale) {
        if (!canFitIntoInt(scale)) {
            throw new ArithmeticException("Illegal result. Scale '" + scale + "' won't fit into Integer");
        }

        BigInteger result = valueA.multiply(valueB);
        return decimal(result, (int) scale);
    }

    private static final long SAFE_TO_MULTIPLY_BY_10_BOUND = Long.MAX_VALUE / 10;
    private static final long SAFE_TO_ADD_DIGIT_BOUND = (Long.MAX_VALUE - 9) / 10;
    private static final long DOABLE_SCALE = -(2L * (long) Integer.MIN_VALUE);
}
