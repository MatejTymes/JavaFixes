package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static mtymes.javafixes.beta.decimal.Constants.BIG_TEN;
import static mtymes.javafixes.beta.decimal.DecimalRounder.roundingCorrection;
import static mtymes.javafixes.beta.decimal.DecimalScaler.descaleBigInteger;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValueFrom;

// todo: test it
class DecimalDivider {

    static Decimal divide(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {

        if (b.signum() == 0) {
            throw new ArithmeticException("Division by zero");
        } else if (a.signum() == 0) {
            return a;
        }

        // todo: although correct (no unscaledValue overflows) currently slow when handling just longs - refactor in the near future

        int finalSign = a.signum() * b.signum();
        BigInteger remainder = bigUnscaledValueFrom(a).abs();
        BigInteger divisor = bigUnscaledValueFrom(b).abs();

        BigInteger[] divAndMod = remainder.divideAndRemainder(divisor);
        BigInteger result = divAndMod[0];
        remainder = divAndMod[1].multiply(BIG_TEN);
        // todo: check scale overflow
        int newScale = a.scale() - b.scale();

        if (newScale > scaleToUse) {
            return descaleBigInteger(
                    finalSign < 0 ? result.negate() : result,
                    newScale,
                    scaleToUse,
                    roundingMode
            );
        } else {
            while (newScale < scaleToUse) {
                BigInteger[] divAndMod2 = remainder.divideAndRemainder(divisor);
                result = result.multiply(BIG_TEN).add(divAndMod2[0]);
                remainder = divAndMod2[1].multiply(BIG_TEN);
                newScale++;
            }

            int remainingDigit = remainder.divide(divisor).intValue();
            BigInteger roundingCorrection = roundingCorrection(
                    finalSign,
                    result,
                    remainingDigit,
                    remainder.signum() != 0,
                    roundingMode
            );
            result = result.add(roundingCorrection);

            return descaleBigInteger(
                    finalSign < 0 ? result.negate() : result,
                    newScale,
                    scaleToUse,
                    roundingMode
            );
        }
    }
}
