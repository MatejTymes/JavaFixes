package co.uk.matejtymes.tdp;

import static co.uk.matejtymes.tdp.LongUtil.*;

// todo: move other toDecimal(...) methods
class DecimalCreator {

    // todo: add support for HugeDecimal
    static Decimal toDecimal(String stringValue) {
        int startIndex = 0;
        boolean positive = true;
        if (stringValue.charAt(0) == '+') {
            startIndex++;
        } else if (stringValue.charAt(0) == '-') {
            positive = false;
            startIndex++;
        }

        long unscaledValue = 0;
        int scale = 0;

        boolean foundDecimalPoint = false;
        for (int i = startIndex; i < stringValue.length(); i++) {
            char c = stringValue.charAt(i);
            if (c >= '0' && c <= '9') {
                unscaledValue = addValue(multiplyBy10Exact(unscaledValue), (c - '0'));
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
        if (!positive) {
            unscaledValue = negateExact(unscaledValue);
        }

        return DecimalsIntern.toDecimal(unscaledValue, scale);
    }

    private static long TEN_MULTIPLICATION_UPPER_BOUND = Long.MAX_VALUE / 10;
    private static long TEN_MULTIPLICATION_LOWER_BOUND = Long.MIN_VALUE / 10;

    // todo: add support for HugeDecimal
    private static long multiplyBy10Exact(long value) {
        if (value <= TEN_MULTIPLICATION_UPPER_BOUND && value >= TEN_MULTIPLICATION_LOWER_BOUND) {
            return value * 10;
        } else {
            return multiplyExact(value, 10L);
        }
    }

    private static long SINGLE_DIGIT_ADDITION_UPPER_BOUND = Long.MAX_VALUE - 9;

    // todo: add support for HugeDecimal
    private static long addValue(long value, int addition) {
        if (value <= SINGLE_DIGIT_ADDITION_UPPER_BOUND) {
            return value + addition;
        } else {
            return addExact(value, addition);
        }
    }
}
