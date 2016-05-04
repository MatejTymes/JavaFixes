package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static mtymes.javafixes.beta.decimal.DecimalMath.*;

// todo: test it
class DecimalEqualizer {

    static int compare(Decimal x, Decimal y) {

        int signComparison = Integer.compare(x.signum(), y.signum());
        if (signComparison != 0) {
            return signComparison;
        }

        int scaleX = x.scale();
        int scaleY = y.scale();

        if (x instanceof LongDecimal && y instanceof LongDecimal) {
            long unscaledX = ((LongDecimal) x).unscaledValue;
            long unscaledY = ((LongDecimal) y).unscaledValue;

            if (scaleX == scaleY) {
                return Long.compare(unscaledX, unscaledY);
            }

            int scaleToGet = min(scaleX, scaleY);

            // upper part comparison
            long rescaledX = descaleValue(unscaledX, scaleX, scaleToGet);
            long rescaledY = descaleValue(unscaledY, scaleY, scaleToGet);

            int topComparison = Long.compare(rescaledX, rescaledY);
            if (topComparison != 0) {
                return topComparison;
            } else {
                // remainder comparison
                long remainderX = unscaledX - upScaleValue(rescaledX, scaleToGet, scaleX);
                long remainderY = unscaledY - upScaleValue(rescaledY, scaleToGet, scaleY);

                return Long.compare(remainderX, remainderY);
            }
        } else {
            BigInteger unscaledX = bigUnscaledValue(x);
            BigInteger unscaledY = bigUnscaledValue(y);

            int maxScale = max(scaleX, scaleY);

            if (scaleX < maxScale) {
                unscaledX = upscaleByPowerOf10(unscaledX, maxScale - scaleX);
            }

            if (scaleY < maxScale) {
                unscaledY = upscaleByPowerOf10(unscaledY, maxScale - scaleY);
            }

            return unscaledX.compareTo(unscaledY);
        }
    }

    static boolean areEqual(Decimal x, Decimal y) {
        return x.compareTo(y) == 0;
    }

    static int hashCode(Decimal d) {
        // its important that this starts as zero - this way we'll ignore trailing zeros
        int hashCode = 0;

        if (d instanceof LongDecimal) {
            long remainder = ((LongDecimal) d).unscaledValue;
            while (remainder != 0) {
                hashCode = (hashCode * 5) + (int) (remainder % 10);
                remainder /= 10;
            }
        } else if (d instanceof HugeDecimal) {
            BigInteger remainder = ((HugeDecimal) d).unscaledValue;
            while (remainder.signum() != 0) {
                hashCode = (hashCode * 5) + remainder.mod(BIG_TEN).intValue();
                remainder = remainder.divide(BIG_TEN);
            }
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }

        return hashCode;
    }

    private static long descaleValue(long value, int fromScale, int toScale) {
        return (toScale < fromScale)
                ? downscaleByPowerOf10(value, fromScale - toScale)
                : value;
    }

    private static long upScaleValue(long value, int fromScale, int toScale) {
        return (toScale > fromScale)
                ? upscaleByPowerOf10(value, toScale - fromScale)
                : value;
    }

    private static BigInteger bigUnscaledValue(Decimal d) {
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
}
