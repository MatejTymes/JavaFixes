package javafixes.math;

import java.math.BigInteger;

import static java.math.BigInteger.TEN;
import static javafixes.math.util.BigIntegerUtil.TEN_AS_BIG_INTEGER;
import static javafixes.math.util.BigIntegerUtil.canConvertToLong;

// todo: add javadoc
// todo: extend Number
public abstract class Decimal {

    private Decimal() {
    }


    static final class LongDecimal extends Decimal {

        transient final long unscaledValue;
        transient final int scale;

        private LongDecimal(long unscaledValue, int scale) {
            this.unscaledValue = unscaledValue;
            // todo: test this
            this.scale = (unscaledValue == 0) ? 0 : scale;
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


    abstract public Number unscaledValue();

    abstract public int scale();

    private static final long SAFE_TO_MULTIPLY_BY_10_BOUND = Long.MAX_VALUE / 10;
    private static final long SAFE_TO_ADD_DIGIT_BOUND = (Long.MAX_VALUE - 9) / 10;
}
