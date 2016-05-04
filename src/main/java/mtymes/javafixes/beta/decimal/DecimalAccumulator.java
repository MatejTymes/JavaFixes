package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.max;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.DecimalMath.upscaleByPowerOf10;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValue;

// todo: test it
class DecimalAccumulator {

    static Decimal add(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {

        int scaleA = a.scale();
        int scaleB = b.scale();

        int maxScale = max(scaleA, scaleB);

        // todo: although correct (no overflows) currently slow when handling just longs - refactor in the near future
        BigInteger unscaledValueA = bigUnscaledValue(a);
        BigInteger unscaledValueB = bigUnscaledValue(b);

        if (scaleA < maxScale) {
            unscaledValueA = upscaleByPowerOf10(unscaledValueA, maxScale - scaleA);
        }
        if (scaleB < maxScale) {
            unscaledValueB = upscaleByPowerOf10(unscaledValueB, maxScale - scaleB);
        }

        return createDecimal(
                unscaledValueA.add(unscaledValueB),
                maxScale
        ).descaleTo(scaleToUse, roundingMode);

//        int maxScale = max(scaleA, scaleB);
//
//        long maxScaledValueX = multiplyExact(a.unscaledValue(), pow10(maxScale - scaleA));
//        long maxScaledValueY = multiplyExact(b.unscaledValue(), pow10(maxScale - scaleB));
//
//        return createDecimal(
//                addExact(maxScaledValueX, maxScaledValueY),
//                maxScale
//        ).descaleTo(scaleToUse, roundingMode);
    }

    static Decimal subtract(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {
        // todo: don't call the DecimalNegator.negate(b) method as it creates another (intermediate) Decimal
        return add(a, DecimalNegator.negate(b), scaleToUse, roundingMode);
    }
}
