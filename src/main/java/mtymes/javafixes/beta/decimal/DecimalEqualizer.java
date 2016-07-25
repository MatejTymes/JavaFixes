package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static mtymes.javafixes.beta.decimal.Constants.BIG_TEN;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValueFrom;
import static mtymes.javafixes.beta.decimal.PowerMath.downscaleByPowerOf10;
import static mtymes.javafixes.beta.decimal.PowerMath.upscaleByPowerOf10;

class DecimalEqualizer {

    // todo: test this
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
            BigInteger unscaledA = bigUnscaledValueFrom(a);
            BigInteger unscaledB = bigUnscaledValueFrom(b);

            int maxScale = max(scaleA, scaleB);

            if (scaleA < maxScale) {
                // todo: check there is no scale overflow
                unscaledA = upscaleByPowerOf10(unscaledA, (long) maxScale - scaleA);
            }

            if (scaleB < maxScale) {
                // todo: check there is no scale overflow
                unscaledB = upscaleByPowerOf10(unscaledB, (long) maxScale - scaleB);
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
                BigInteger[] divAndMod = remainder.divideAndRemainder(BIG_TEN);
                remainder = divAndMod[0];
                hashCode = (hashCode * 5) + divAndMod[1].intValue();
            }
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }

        return hashCode;
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
}
