package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.max;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValueFrom;
import static mtymes.javafixes.beta.decimal.OverflowUtil.hasAdditionOverflown;
import static mtymes.javafixes.beta.decimal.OverflowUtil.willNegationOverflow;
import static mtymes.javafixes.beta.decimal.PowerMath.*;

// todo: test it
class DecimalAccumulator {

    static Decimal add(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {
        if (a instanceof LongDecimal && b instanceof LongDecimal) {
            long unscaledValueA = ((LongDecimal) a).unscaledValue;
            long unscaledValueB = ((LongDecimal) b).unscaledValue;

            return sumOf(unscaledValueA, unscaledValueB, a.scale(), b.scale(), scaleToUse, roundingMode);
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

            if (willNegationOverflow(unscaledValueB)) {
                return sumOf(BigInteger.valueOf(unscaledValueA), BigInteger.valueOf(unscaledValueB).negate(), a.scale(), b.scale(), scaleToUse, roundingMode);
            }

            return sumOf(unscaledValueA, -unscaledValueB, a.scale(), b.scale(), scaleToUse, roundingMode);
        } else {
            BigInteger unscaledValueA = bigUnscaledValueFrom(a);
            BigInteger unscaledValueB = bigUnscaledValueFrom(b);

            return sumOf(unscaledValueA, unscaledValueB.negate(), a.scale(), b.scale(), scaleToUse, roundingMode);
        }
    }

    private static Decimal sumOf(long unscaledValueA, long unscaledValueB, int scaleA, int scaleB, int scaleToUse, RoundingMode roundingMode) {
        int sumScale = max(scaleA, scaleB);

        int scaleFactorA = sumScale - scaleA;
        int scaleFactorB = sumScale - scaleB;
        if (!canUpscaleLongByPowerOf10(unscaledValueA, scaleFactorA) || !canUpscaleLongByPowerOf10(unscaledValueB, scaleB)) {
            return sumOf(BigInteger.valueOf(unscaledValueA), BigInteger.valueOf(unscaledValueB), scaleA, scaleB, scaleToUse, roundingMode);
        }

        long rescaledValueA;
        if (scaleFactorA == 0) {
            rescaledValueA = unscaledValueA;
        } else {
            rescaledValueA = unscaledValueA * powerOf10Long(scaleFactorA);
        }

        long rescaledValueB;
        if (scaleFactorB == 0) {
            rescaledValueB = unscaledValueB;
        } else {
            rescaledValueB = unscaledValueB * powerOf10Long(scaleFactorB);
        }

        return sumOf(rescaledValueA, rescaledValueB, sumScale, scaleToUse, roundingMode);
    }


    private static Decimal sumOf(long valueA, long valueB, int sumScale, int scaleToUse, RoundingMode roundingMode) {
        long result = valueA + valueB;

        if (hasAdditionOverflown(result, valueA, valueB)) {
            return sumOf(BigInteger.valueOf(valueA), BigInteger.valueOf(valueB), sumScale, scaleToUse, roundingMode);
        }

        return createDecimal(result, sumScale);
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
        return createDecimal(valueA.add(valueB), sumScale, scaleToUse, roundingMode);
    }
}
