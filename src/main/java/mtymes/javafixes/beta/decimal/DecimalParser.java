package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;

import static java.math.BigInteger.TEN;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;

// todo: test it
class DecimalParser {

    // todo: improve for number XYZ000000000000000000000 - currently not great/fast for them
    static Decimal parseString(String stringValue) {

        int scale = 0;
        long unscaledValueL = 0;
        BigInteger unscaledValueB = null;

        int startIndex = 0;
        boolean negate = false;
        boolean foundDecimalPoint = false;

        if (stringValue.length() == 0) {
            throw new NumberFormatException("No value defined");
        } else if (stringValue.charAt(0) == '+') {
            startIndex++;
        } else if (stringValue.charAt(0) == '-') {
            negate = true;
            startIndex++;
        }

        boolean foundValue = false;
        for (int i = startIndex; i < stringValue.length(); i++) {
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

        if (!foundValue) {
            throw new NumberFormatException("No value defined");
        }

        return unscaledValueB == null
                ? createDecimal(negate ? -unscaledValueL : unscaledValueL, scale)
                : toDecimal(negate, unscaledValueB, scale);
    }

    private static final long SAFE_TO_MULTIPLY_BY_10_BOUND = Long.MAX_VALUE / 10;
    private static final long SAFE_TO_ADD_DIGIT_BOUND = (Long.MAX_VALUE - 9) / 10;

    // added to speed up the code
    private static Decimal toDecimal(boolean negate, BigInteger unscaledValue, int scale) {
        return createDecimal(negate ? unscaledValue.negate() : unscaledValue, scale);
    }
}
