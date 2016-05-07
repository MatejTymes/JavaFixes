package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValueFrom;
import static mtymes.javafixes.beta.decimal.OverflowUtil.hasMultiplicationOverflown;

// todo: test it
class DecimalMultiplier {

    static Decimal multiply(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {
        if (a instanceof LongDecimal && b instanceof LongDecimal) {
            long unscaledValueA = ((LongDecimal) a).unscaledValue;
            long unscaledValueB = ((LongDecimal) b).unscaledValue;

            long result = unscaledValueA * unscaledValueB;
            if (hasMultiplicationOverflown(result, unscaledValueA, unscaledValueB)) {
                return createDecimal(
                        BigInteger.valueOf(unscaledValueA).multiply(BigInteger.valueOf(unscaledValueB)),
                        a.scale() + b.scale(),
                        scaleToUse,
                        roundingMode
                );
            }

            return createDecimal(result, a.scale() + b.scale(), scaleToUse, roundingMode);
        } else {
            BigInteger unscaledValueA = bigUnscaledValueFrom(a);
            BigInteger unscaledValueB = bigUnscaledValueFrom(b);

            return createDecimal(unscaledValueA.multiply(unscaledValueB), a.scale() + b.scale(), scaleToUse, roundingMode);
        }
    }
}
