package javafixes.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.math.RoundingMode.*;
import static javafixes.math.BigIntegerUtil.TEN_AS_BIG_INTEGER;
import static javafixes.math.BigIntegerUtil.canConvertToLong;
import static javafixes.math.LongUtil.canFitIntoInt;
import static javafixes.math.PowerUtil.*;

// todo: add javadoc, formatter and make Serializable
public abstract class Decimal extends Number implements Comparable<Decimal> {

    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    public static final Decimal ZERO = decimal(0, 0);
    public static final Decimal ONE = decimal(1, 0);
    public static final Decimal TEN = decimal(10, 0);

    // private constructor so it is non-extendible
    private Decimal() {
    }

    /* =========================== */
    /* ---   implementations   --- */
    /* =========================== */

    private static final class LongDecimal extends Decimal {

        // todo: don't allow Long.MIN_VALUE to be stored in here - it will make the handling of the class easier
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

            // todo: refactor, but good for now

            long scaleDiff = (long) scale - scaleToUse;

            long valueWithRoundingDigit = downscaleByPowerOf10(unscaledValue, scaleDiff - 1);
            boolean hasAdditionalRemainder = unscaledValue != upscaleByPowerOf10(valueWithRoundingDigit, scaleDiff - 1);
            if (valueWithRoundingDigit == 0 && !hasAdditionalRemainder) {
                return Decimal.ZERO;
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

            return decimal(rescaledValue, scaleToUse);
        }

        @Override
        public Decimal negate() {
            if (unscaledValue != Long.MIN_VALUE) {
                return new LongDecimal(-unscaledValue, scale);
            } else {
                return new HugeDecimal(BigInteger.valueOf(unscaledValue).negate(), scale);
            }
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

            // todo: refactor, but good for now

            // todo: quick descale: if scaleDiff > precision, but good for now
            long scaleDiff = (long) scale - (long) scaleToUse;

            boolean hasAdditionalRemainder = false;
            BigInteger valueWithRoundingDigit = unscaledValue;
            long n = scaleDiff - 1;
            // todo: simplify this, but good for now
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
                return ZERO;
            }

            BigInteger[] divAndMod = valueWithRoundingDigit.divideAndRemainder(TEN_AS_BIG_INTEGER);
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

            return decimal(rescaledValue, scaleToUse);
        }

        @Override
        public Decimal negate() {
            return new HugeDecimal(unscaledValue.negate(), scale);
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
                throw new ArithmeticException("Scale overflow - can't set scale to less than: " + Integer.MIN_VALUE);
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
            BigInteger[] divAndMod = unscaledValue.divideAndRemainder(TEN_AS_BIG_INTEGER);
            if (divAndMod[1].signum() != 0) {
                break;
            }
            unscaledValue = divAndMod[0];

            if (scale == Integer.MIN_VALUE) {
                throw new ArithmeticException("Scale overflow - can't set scale to less than: " + Integer.MIN_VALUE);
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
                            unscaledValueB = BigInteger.valueOf(unscaledValueL).multiply(TEN_AS_BIG_INTEGER).add(BigInteger.valueOf(digitToAdd));
                        }
                    }
                } else {
                    unscaledValueB = unscaledValueB.multiply(TEN_AS_BIG_INTEGER).add(BigInteger.valueOf(digitToAdd));
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
                        if (exponent > DOABLE_EXPONENT) {
                            throw new NumberFormatException("Illegal value. Scale won't fit into Integer");
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
                    throw new NumberFormatException("Illegal value. Scale '" + exponent + "' won't fit into Integer");
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

    abstract public BigDecimal bigDecimalValue();

    @Override
    public int intValue() {
        // todo: cheating, but good for now - fix this
        return bigDecimalValue().intValue();
    }

    @Override
    public long longValue() {
        // todo: cheating, but good for now - fix this
        return bigDecimalValue().longValue();
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
                BigInteger[] divAndMod = remainder.divideAndRemainder(TEN_AS_BIG_INTEGER);
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
        return (scale > -19 && scale < 19) ? toPlainString() : toScientificNotation();
    }

    public String toPlainString() {
        return toPlainString(0);
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
            // todo: slow for huge scale differences
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
                throw new ArithmeticException("Rounding necessary");
            }
        }

        return roundingCorrection;
    }

    private static final long SAFE_TO_MULTIPLY_BY_10_BOUND = Long.MAX_VALUE / 10;
    private static final long SAFE_TO_ADD_DIGIT_BOUND = (Long.MAX_VALUE - 9) / 10;
    private static final long DOABLE_EXPONENT = -(2L * (long) Integer.MIN_VALUE);
}
