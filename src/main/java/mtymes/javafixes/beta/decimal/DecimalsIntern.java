package mtymes.javafixes.beta.decimal;

import java.math.RoundingMode;

import static java.lang.String.format;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.LongUtil.*;

// todo: test this
class DecimalsIntern {

    // todo: handle zero divisor exception
    static Decimal divide(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {

        long remainder = x.unscaledValue();
        long divisor = y.unscaledValue();

        long result = remainder / divisor;
        remainder = multiplyBy10Exact(remainder % divisor);
        int newScale = x.scale() - y.scale();

        if (newScale > scaleToUse) {
            return createDecimal(result, newScale)
                    .descaleTo(scaleToUse, roundingMode);
        } else {
            while (newScale < scaleToUse) {
                result = addExact(multiplyBy10Exact(result), (remainder / divisor));
                remainder = multiplyBy10Exact(remainder % divisor);
                newScale++;
            }
            long remainingDigit = remainder / divisor;
            result = roundBasedOnRemainder(result, remainingDigit, roundingMode);


            return createDecimal(result, newScale)
                    .descaleTo(scaleToUse, roundingMode);
        }
    }

    // todo: in the future make sure the digit is only from 0 to 9 (currently the sign of the digit makes it a little bit awkward)
    // todo: turn remainingDigit into byte
    static long roundBasedOnRemainder(long valueBeforeRounding, long remainingDigit, RoundingMode roundingMode) {
        if (remainingDigit < -9 || remainingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid remaining digit (%d). Should be only -9 to 9", remainingDigit));
        }

        long valueAfterRounding = valueBeforeRounding;

        if (remainingDigit != 0) {
            if (roundingMode == RoundingMode.UP) {
                if (remainingDigit > 0 && valueBeforeRounding >= 0) {
                    valueAfterRounding = incrementExact(valueBeforeRounding);
                } else if (remainingDigit < 0 && valueBeforeRounding < 0) {
                    valueAfterRounding = decrementExact(valueBeforeRounding);
                }
            } else if (roundingMode == RoundingMode.DOWN) {
                if (remainingDigit < 0 && valueBeforeRounding >= 0) {
                    valueAfterRounding = decrementExact(valueBeforeRounding);
                } else if (remainingDigit > 0 && valueBeforeRounding < 0) {
                    valueAfterRounding = incrementExact(valueBeforeRounding);
                }
            } else if (roundingMode == RoundingMode.CEILING) {
                if (remainingDigit > 0 && valueBeforeRounding >= 0) {
                    valueAfterRounding = incrementExact(valueBeforeRounding);
                }
            } else if (roundingMode == RoundingMode.FLOOR) {
                if (remainingDigit < 0 && valueBeforeRounding < 0) {
                    valueAfterRounding = decrementExact(valueBeforeRounding);
                }
            } else if (roundingMode == RoundingMode.HALF_UP) {
                if (remainingDigit >= 5 && valueBeforeRounding >= 0) {
                    valueAfterRounding = incrementExact(valueBeforeRounding);
                } else if (remainingDigit <= -5 && valueBeforeRounding < 0) {
                    valueAfterRounding = decrementExact(valueBeforeRounding);
                }
            } else if (roundingMode == RoundingMode.HALF_DOWN) {
                if (remainingDigit > 5 && valueBeforeRounding >= 0) {
                    valueAfterRounding = incrementExact(valueBeforeRounding);
                } else if (remainingDigit < -5 && valueBeforeRounding < 0) {
                    valueAfterRounding = decrementExact(valueBeforeRounding);
                }
            } else if (roundingMode == RoundingMode.HALF_EVEN) {
                if (valueBeforeRounding >= 0) {
                    if (remainingDigit > 5 || (remainingDigit == 5 && valueBeforeRounding % 2 != 0)) {
                        valueAfterRounding = incrementExact(valueBeforeRounding);
                    }
                } else {
                    if (remainingDigit < -5 || (remainingDigit == -5 && valueBeforeRounding % 2 != 0)) {
                        valueAfterRounding = decrementExact(valueBeforeRounding);
                    }
                }
            } else if (roundingMode == RoundingMode.UNNECESSARY) {
                throw new ArithmeticException("Rounding necessary");
            }
        }

        return valueAfterRounding;
    }

    private static final long TEN_MULTIPLICATION_UPPER_BOUND = Long.MAX_VALUE / 10;
    private static final long TEN_MULTIPLICATION_LOWER_BOUND = Long.MIN_VALUE / 10;

    // todo: add support for HugeDecimal
    private static long multiplyBy10Exact(long value) {
        if (value <= TEN_MULTIPLICATION_UPPER_BOUND && value >= TEN_MULTIPLICATION_LOWER_BOUND) {
            return value * 10;
        } else {
            return multiplyExact(value, 10L);
        }
    }

}
