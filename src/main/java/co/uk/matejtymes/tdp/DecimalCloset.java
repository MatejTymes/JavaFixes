package co.uk.matejtymes.tdp;

import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.Decimal.decimal;
import static co.uk.matejtymes.tdp.LongUtil.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

// todo: test this
public class DecimalCloset {

    // todo: test
    static Decimal add(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        int scaleX = x.getScale();
        int scaleY = y.getScale();
        // todo: this is easy to read, but maybe
        // todo: use rather min(scaleToUse, scaleX, scaleY) to actually prevent unnecessary overflow
        int maxScale = max(scaleX, scaleY);

        long maxScaledValueX = multiplyExact(x.getUnscaledValue(), pow10(maxScale - scaleX));
        long maxScaledValueY = multiplyExact(y.getUnscaledValue(), pow10(maxScale - scaleY));

        return decimal(
                addExact(maxScaledValueX, maxScaledValueY),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    static Decimal subtract(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        int scaleX = x.getScale();
        int scaleY = y.getScale();
        // todo: this is easy to read, but maybe
        // todo: use rather min(scaleToUse, scaleX, scaleY) to actually prevent unnecessary overflow
        int maxScale = max(scaleX, scaleY);

        long maxScaledValueX = multiplyExact(x.getUnscaledValue(), pow10(maxScale - scaleX));
        long maxScaledValueY = multiplyExact(y.getUnscaledValue(), pow10(maxScale - scaleY));

        return decimal(
                subtractExact(maxScaledValueX, maxScaledValueY),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    static Decimal multiply(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        Decimal strippedX = x.stripTrailingZeros();
        Decimal strippedY = y.stripTrailingZeros();

        // todo: extremely simplistic algorithm - please improve
        // todo: - this is extremely prone to overflow

        // todo: use BigInteger in case of overflow

        return decimal(
                multiplyExact(strippedX.getUnscaledValue(), strippedY.getUnscaledValue()),
                strippedX.getScale() + strippedY.getScale()
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    // todo: handle null divisor exception
    static Decimal divide(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {

        long unscaledX = x.getUnscaledValue();
        long unscaledY = y.getUnscaledValue();

        boolean positive = (unscaledX < 0 && unscaledY < 0) ||
                (unscaledX >= 0 && unscaledY >= 0);

        // todo: this fails once unscaledX is Long.MIN_VALUE
        long remainder = absExact(unscaledX);
        // todo: this fails once unscaledY is Long.MIN_VALUE
        long divisor = absExact(unscaledY);

        long result = remainder / divisor;
        remainder = multiplyExact((remainder % divisor), 10);
        int newScale = x.getScale() - y.getScale();

        if (newScale > scaleToUse) {
            // todo: test this
            if (!positive) {
                result = negateExact(result);
            }
            return decimal(result, newScale)
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


            return decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        }
    }

    // todo: move rescaleTo in here
    // todo: move hashCode in here

    static int compare(Decimal x, Decimal y) {
        int scaleX = x.getScale();
        int scaleY = y.getScale();

        long unscaledX = x.getUnscaledValue();
        long unscaledY = y.getUnscaledValue();

        if (scaleX == scaleY) {
            return Long.compare(unscaledX, unscaledY);
        }

        int minScale = min(scaleX, scaleY);

        long scalerX = pow10(scaleX - minScale);
        long scalerY = pow10(scaleY - minScale);

        // by doing division instead of multiplication we prevent overflow
        long xScaledDownValue = unscaledX / scalerX;
        long yScaledDownValue = unscaledY / scalerY;

        int comparison = Long.compare(xScaledDownValue, yScaledDownValue);
        if (comparison != 0) {
            return comparison;
        } else {
            long xRemainder = subtractExact(unscaledX, multiplyExact(xScaledDownValue, scalerX));
            long yRemainder = subtractExact(unscaledY, multiplyExact(yScaledDownValue, scalerY));

            return Long.compare(xRemainder, yRemainder);
        }
    }

    static int hashCode(Decimal d) {
        // its important that this starts as zero - this way will ignore trailing zeros
        int hashCode = 0;

        long remainder = d.getUnscaledValue();
        while (remainder > 0) {
            hashCode = (hashCode * 5) + (int) (remainder % 10);
            remainder /= 10;
        }

        return hashCode;
    }


    static long roundBasedOnRemainder(long valueBeforeRounding, long remainingDigit, RoundingMode roundingMode) {
        if (remainingDigit < -9 || remainingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid remaining digit (%d). Should be only -9 to 9"));
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
}
