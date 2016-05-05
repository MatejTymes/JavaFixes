package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static mtymes.javafixes.beta.decimal.DecimalMath.*;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValue;

// todo: test it
class DecimalEqualizer {

    static int compare(Decimal a, Decimal b) {

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
            BigInteger unscaledA = bigUnscaledValue(a);
            BigInteger unscaledB = bigUnscaledValue(b);

            int maxScale = max(scaleA, scaleB);

            if (scaleA < maxScale) {
                unscaledA = upscaleByPowerOf10(unscaledA, maxScale - scaleA);
            }

            if (scaleB < maxScale) {
                unscaledB = upscaleByPowerOf10(unscaledB, maxScale - scaleB);
            }

            return unscaledA.compareTo(unscaledB);
        }
    }

    static boolean areEqual(Decimal a, Decimal b) {
        return a.compareTo(b) == 0;
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
}
