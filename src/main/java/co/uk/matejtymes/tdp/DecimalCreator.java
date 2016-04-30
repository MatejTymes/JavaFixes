package co.uk.matejtymes.tdp;

import java.math.BigInteger;

import static co.uk.matejtymes.tdp.BigIntegerUtil.canConvertToLong;
import static java.math.BigInteger.TEN;

// todo: test it
class DecimalCreator {

    static Decimal toDecimal(long unscaledValue, int scale) {
        // todo: strip trailing zeros in better way
        while (unscaledValue != 0 && unscaledValue % 10 == 0) {
            unscaledValue /= 10;
            scale--;
        }

        return new Decimal.LongDecimal(unscaledValue, scale);
    }

    static Decimal toDecimal(BigInteger unscaledValue, int scale) {
        // todo: strip trailing zeros in better way
        while (!unscaledValue.equals(BigInteger.ZERO) && BigInteger.ZERO.equals(unscaledValue.mod(TEN))) {
            unscaledValue = unscaledValue.divide(TEN);
            scale--;
        }

        if (canConvertToLong(unscaledValue)) {
            return new Decimal.LongDecimal(unscaledValue.longValue(), scale);
        } else {
            return new Decimal.HugeDecimal(unscaledValue, scale);
        }
    }

    // todo: improve for number XYZ000000000000000000000 - currently not great/fast for them
    static Decimal toDecimal(String stringValue) {

        int scale = 0;
        long unscaledValueL = 0;
        BigInteger unscaledValueB = null;

        int startIndex = 0;
        boolean negate = false;
        boolean foundDecimalPoint = false;

        if (stringValue.charAt(0) == '+') {
            startIndex++;
        } else if (stringValue.charAt(0) == '-') {
            negate = true;
            startIndex++;
        }

        for (int i = startIndex; i < stringValue.length(); i++) {
            char c = stringValue.charAt(i);
            if (c >= '0' && c <= '9') {
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
                    ++scale;
                }
            } else if (c == '.') {
                if (foundDecimalPoint) {
                    // todo: test this
                    throw new NumberFormatException("Illegal value. Too many decimal points");
                }
                foundDecimalPoint = true;
            } else if (c != '_') {
                // todo: test this
                throw new NumberFormatException("Decimal contains invalid character: " + c);
            }
        }

        return unscaledValueB == null
                ? toDecimal(negate ? -unscaledValueL : unscaledValueL, scale)
                : toDecimal(negate, unscaledValueB, scale);
    }

    private static long SAFE_TO_MULTIPLY_BY_10_BOUND = Long.MAX_VALUE / 10;
    private static long SAFE_TO_ADD_DIGIT_BOUND = (Long.MAX_VALUE - 9) / 10;

    // added to speed up the code
    private static Decimal toDecimal(boolean negate, BigInteger unscaledValue, int scale) {
        return toDecimal(negate ? unscaledValue.negate() : unscaledValue, scale);
    }
}
