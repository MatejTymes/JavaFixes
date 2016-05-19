package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.max;
import static mtymes.javafixes.beta.decimal.DecimalScaler.descaleBigInteger;
import static mtymes.javafixes.beta.decimal.DecimalScaler.descaleLong;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValueFrom;
import static mtymes.javafixes.beta.decimal.OverflowUtil.didOverflowOnLongAddition;
import static mtymes.javafixes.beta.decimal.OverflowUtil.willNegationOverflow;
import static mtymes.javafixes.beta.decimal.PowerMath.*;

// todo: test it
class DecimalAccumulator {

    static Decimal add(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {
        if (a instanceof LongDecimal && b instanceof LongDecimal) {

            return sumOf(
                    ((LongDecimal) a).unscaledValue,
                    ((LongDecimal) b).unscaledValue,
                    ((LongDecimal) a).scale,
                    ((LongDecimal) b).scale,
                    scaleToUse,
                    roundingMode
            );
        } else {
            BigInteger unscaledValueA = bigUnscaledValueFrom(a);
            BigInteger unscaledValueB = bigUnscaledValueFrom(b);

            return sumOf(unscaledValueA, unscaledValueB, a.scale(), b.scale(), scaleToUse, roundingMode);
        }
    }

    static Decimal subtract(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {
        if (a instanceof LongDecimal && b instanceof LongDecimal) {
            long unscaledValueA = ((LongDecimal) a).unscaledValue;
            long unscaledValueB = ((LongDecimal) b).unscaledValue;
            int scaleA = ((LongDecimal) a).scale;
            int scaleB = ((LongDecimal) b).scale;

            if (willNegationOverflow(unscaledValueB)) {
                return sumOf(BigInteger.valueOf(unscaledValueA), BigInteger.valueOf(unscaledValueB).negate(), scaleA, scaleB, scaleToUse, roundingMode);
            }

            return sumOf(unscaledValueA, -unscaledValueB, scaleA, scaleB, scaleToUse, roundingMode);
        } else {
            BigInteger unscaledValueA = bigUnscaledValueFrom(a);
            BigInteger unscaledValueB = bigUnscaledValueFrom(b);

            return sumOf(unscaledValueA, unscaledValueB.negate(), a.scale(), b.scale(), scaleToUse, roundingMode);
        }
    }

    private static Decimal sumOf(long unscaledValueA, long unscaledValueB, int scaleA, int scaleB, int scaleToUse, RoundingMode roundingMode) {
        long scaleDiff = (long) scaleA - scaleB;

        if (scaleDiff == 0) {
            return sumOf(unscaledValueA, unscaledValueB, scaleA, scaleToUse, roundingMode);
        } else if (scaleDiff < 0) {
            if (!canUpscaleLongByPowerOf10(unscaledValueA, (int)-scaleDiff)) {
                return sumOf(BigInteger.valueOf(unscaledValueA), BigInteger.valueOf(unscaledValueB), scaleA, scaleB, scaleToUse, roundingMode);
            }
            return sumOf(unscaledValueA * powerOf10Long((int)-scaleDiff), unscaledValueB, scaleB, scaleToUse, roundingMode);
        } else {
            if (!canUpscaleLongByPowerOf10(unscaledValueB, (int)scaleDiff)) {
                return sumOf(BigInteger.valueOf(unscaledValueA), BigInteger.valueOf(unscaledValueB), scaleA, scaleB, scaleToUse, roundingMode);
            }
            return sumOf(unscaledValueA, unscaledValueB * powerOf10Long((int)scaleDiff), scaleA, scaleToUse, roundingMode);
        }
    }


    private static Decimal sumOf(long valueA, long valueB, int sumScale, int scaleToUse, RoundingMode roundingMode) {
        long result = valueA + valueB;

        if (didOverflowOnLongAddition(result, valueA, valueB)) {
            return sumOf(BigInteger.valueOf(valueA), BigInteger.valueOf(valueB), sumScale, scaleToUse, roundingMode);
        }

        return descaleLong(result, sumScale, scaleToUse, roundingMode);
    }

    private static Decimal sumOf(BigInteger unscaledValueA, BigInteger unscaledValueB, int scaleA, int scaleB, int scaleToUse, RoundingMode roundingMode) {
        int sumScale = max(scaleA, scaleB);
        if (scaleA < sumScale) {
            unscaledValueA = upscaleByPowerOf10(unscaledValueA, sumScale - scaleA);
        }
        if (scaleB < sumScale) {
            unscaledValueB = upscaleByPowerOf10(unscaledValueB, sumScale - scaleB);
        }

        return sumOf(unscaledValueA, unscaledValueB, sumScale, scaleToUse, roundingMode);
    }

    private static Decimal sumOf(BigInteger valueA, BigInteger valueB, int sumScale, int scaleToUse, RoundingMode roundingMode) {
        return descaleBigInteger(valueA.add(valueB), sumScale, scaleToUse, roundingMode);
    }
}
