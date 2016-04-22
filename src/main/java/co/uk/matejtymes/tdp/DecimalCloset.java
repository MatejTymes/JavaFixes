package co.uk.matejtymes.tdp;

import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.Decimal.decimal;
import static co.uk.matejtymes.tdp.LongUtil.addExact;
import static co.uk.matejtymes.tdp.LongUtil.decrementExact;
import static co.uk.matejtymes.tdp.LongUtil.incrementExact;
import static co.uk.matejtymes.tdp.LongUtil.multiplyExact;
import static co.uk.matejtymes.tdp.LongUtil.negateExact;
import static co.uk.matejtymes.tdp.LongUtil.*;
import static co.uk.matejtymes.tdp.LongUtil.subtractExact;
import static java.lang.Math.*;
import static java.lang.String.format;

// todo: test this
public class DecimalCloset {

    static Decimal stringToDecimal(String stringValue) {
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
            } else if (c == '_') {
                // todo: check it is used only between numbers
                // ignore
            } else {
                throw new IllegalArgumentException("Decimal contains invalid character: " + c);
            }
        }
        if (!positive) {
            unscaledValue = negateExact(unscaledValue);
        }

        return decimal(unscaledValue, scale);
    }

    static Decimal negate(Decimal d) {
        return decimal(negateExact(d.unscaledValue()), d.scale());
    }

    static Decimal add(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        checkScale(scaleToUse);

        int scaleX = x.scale();
        int scaleY = y.scale();
        // todo: this is easy to read, but maybe
        // todo: use rather min(scaleToUse, scaleX, scaleY) to actually prevent unnecessary overflow
        int maxScale = max(scaleX, scaleY);

        long maxScaledValueX = multiplyExact(x.unscaledValue(), pow10(maxScale - scaleX));
        long maxScaledValueY = multiplyExact(y.unscaledValue(), pow10(maxScale - scaleY));

        return decimal(
                addExact(maxScaledValueX, maxScaledValueY),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    static Decimal subtract(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        return add(x, negate(y), scaleToUse, roundingMode);
    }

    static Decimal multiply(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        checkScale(scaleToUse);

        Decimal strippedX = x.stripTrailingZeros();
        Decimal strippedY = y.stripTrailingZeros();

        // todo: extremely simplistic algorithm - please improve
        // todo: - this is extremely prone to overflow

        // todo: use BigInteger in case of overflow
        long unscaledValue = multiplyExact(strippedX.unscaledValue(), strippedY.unscaledValue());
//        long unscaledValue = BigInteger.valueOf(strippedX.unscaledValue())
//                .multiply(BigInteger.valueOf(strippedY.unscaledValue()))
//                .longValueExact();

        return decimal(
                unscaledValue,
                strippedX.scale() + strippedY.scale()
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: handle null divisor exception
    static Decimal divide(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        checkScale(scaleToUse);

        long remainder = x.unscaledValue();
        long divisor = y.unscaledValue();

        long result = remainder / divisor;
        remainder = multiplyExact((remainder % divisor), 10);
        int newScale = x.scale() - y.scale();

        if (newScale > scaleToUse) {
            return decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        } else {
            while (newScale < scaleToUse) {
                result = addExact(multiplyExact(result, 10), (remainder / divisor));
                remainder = multiplyExact((remainder % divisor), 10);
                newScale++;
            }
            long remainingDigit = remainder / divisor;
            result = roundBasedOnRemainder(result, remainingDigit, roundingMode);


            return decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        }
    }

    static Decimal rescaleTo(Decimal d, int scaleToUse, RoundingMode roundingMode) {
        checkScale(scaleToUse);

        int scale = d.scale();
        long unscaledValue = d.unscaledValue();

        if (scaleToUse == scale) {
            return d;
        } else if (scaleToUse > scale) {
            long scaler = pow10(scaleToUse - scale);
            long rescaledValue = multiplyExact(unscaledValue, scaler);
            return decimal(rescaledValue, scaleToUse);
        }

        long scaler = pow10(scale - scaleToUse);
        long rescaledValue = unscaledValue / scaler;
        long remainder = subtractExact(unscaledValue, multiplyExact(rescaledValue, scaler));
        long remainingDigit = remainder / (scaler / 10);

        rescaledValue = roundBasedOnRemainder(rescaledValue, remainingDigit, roundingMode);

        return decimal(rescaledValue, scaleToUse);
    }

    static Decimal stripTrailingZerosWithScaleAtLeast(Decimal d, int minScaleToKeep) {
        checkScale(minScaleToKeep);

        int scale = d.scale();
        long unscaledValue = d.unscaledValue();

        if (scale < minScaleToKeep) {
            return rescaleTo(d, minScaleToKeep, RoundingMode.UNNECESSARY);
        } else if (scale == minScaleToKeep || unscaledValue % 10 != 0) {
            return d;
        }

        long newUnscaledValue = unscaledValue;
        int newScale = scale;

        while (newUnscaledValue % 10 == 0 && newScale > minScaleToKeep) {
            newUnscaledValue /= 10;
            newScale--;
        }

        return decimal(newUnscaledValue, newScale);
    }

    static int compare(Decimal x, Decimal y) {
        int scaleX = x.scale();
        int scaleY = y.scale();

        long unscaledX = x.unscaledValue();
        long unscaledY = y.unscaledValue();

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

    static boolean areIdentical(Decimal x, Decimal y) {
        if (x == y) {
            return true;
        }
        return x.unscaledValue() == y.unscaledValue() &&
                x.scale() == y.scale();
    }

    static boolean areEqual(Decimal x, Decimal y) {
        return x.compareTo(y) == 0;
    }

    static int hashCode(Decimal d) {
        // its important that this starts as zero - this way will ignore trailing zeros
        int hashCode = 0;

        long remainder = d.unscaledValue();
        while (remainder > 0) {
            hashCode = (hashCode * 5) + (int) (remainder % 10);
            remainder /= 10;
        }

        return hashCode;
    }

    static String toPlainString(Decimal d) {
        StringBuilder sb = new StringBuilder();

        long unscaledValue = d.unscaledValue();

        int charsToDecimalPoint = d.scale();
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

    // todo: in the future make sure the digit is only from 0 to 9 (currently the sign of the digit makes it a little bit awkward)
    private static long roundBasedOnRemainder(long valueBeforeRounding, long remainingDigit, RoundingMode roundingMode) {
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

    static void checkScale(int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(format("The minimum Scale value (%d) must be at least 0", scale));
        }
    }
}
