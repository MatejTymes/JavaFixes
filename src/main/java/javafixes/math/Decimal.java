package javafixes.math;

import java.math.BigInteger;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.math.BigInteger.TEN;
import static javafixes.math.BigIntegerUtil.TEN_AS_BIG_INTEGER;
import static javafixes.math.BigIntegerUtil.canConvertToLong;
import static javafixes.math.PowerUtil.downscaleByPowerOf10;
import static javafixes.math.PowerUtil.upscaleByPowerOf10;

// todo: add javadoc
// todo: extend Number
public abstract class Decimal implements Comparable<Decimal> {

    private Decimal() {
    }

    /* =========================== */
    /* ---   implementations   --- */
    /* =========================== */

    static final class LongDecimal extends Decimal {

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
        public Decimal negate() {
            if (unscaledValue != Long.MIN_VALUE) {
                return new LongDecimal(-unscaledValue, scale);
            } else {
                return new HugeDecimal(BigInteger.valueOf(unscaledValue).negate(), scale);
            }
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

    // todo: add ability to parse scientific notation (1e20)
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
                            unscaledValueB = BigInteger.valueOf(unscaledValueL).multiply(TEN).add(BigInteger.valueOf(digitToAdd));
                        }
                    }
                } else {
                    unscaledValueB = unscaledValueB.multiply(TEN).add(BigInteger.valueOf(digitToAdd));
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

    /* ========================== */
    /* ---   public methods   --- */
    /* ========================== */

    abstract public int signum();

    abstract public Number unscaledValue();

    abstract public int scale();

    abstract public Decimal negate();

    public Decimal abs() {
        return (signum() < 0) ? negate() : this;
    }

    // todo: too slow - speed up
    @Override
    public int compareTo(Decimal other) {
        return compare(this, other);
    }

    // todo: test
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        //  we only check "other" is instance of Decimal and not check concrete class
        //  as this allows us to have subclassing
        if (other == null || !(other instanceof Decimal)) return false;

        return this.compareTo((Decimal) other) == 0;
    }

    // todo: test
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
        // todo: verify can't have scale overflow on abs?
        return (Math.abs(scale()) < 19) ? toPlainString() : toScientificNotation();
    }

    public String toPlainString() {
        return toPlainString(0);
    }

    // todo: add method with Scale parameter as well
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

        int signComparison = Integer.compare(a.signum(), b.signum());
        if (signComparison != 0) {
            return signComparison;
        }

        int scaleA = a.scale();
        int scaleB = b.scale();

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
            // todo: make this faster for huge scale differences - currently its extremely slow
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

    private static final long SAFE_TO_MULTIPLY_BY_10_BOUND = Long.MAX_VALUE / 10;
    private static final long SAFE_TO_ADD_DIGIT_BOUND = (Long.MAX_VALUE - 9) / 10;
}
