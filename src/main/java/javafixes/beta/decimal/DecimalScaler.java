package javafixes.beta.decimal;

import javafixes.beta.decimal.Decimal.HugeDecimal;
import javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static javafixes.beta.decimal.Constants.BIG_TEN;
import static javafixes.beta.decimal.DecimalCreator.createDecimal;
import static javafixes.beta.decimal.PowerMath.downscaleByPowerOf10;
import static javafixes.beta.decimal.PowerMath.upscaleByPowerOf10;

// todo: test it
class DecimalScaler {

    static Decimal descaleTo(Decimal d, int scaleToUse, RoundingMode roundingMode) {

        int scale = d.scale();
        if (scaleToUse >= scale) {
            return d; // no need to scale
        }

        if (d instanceof LongDecimal) {

            return downscale(((LongDecimal) d).unscaledValue, scale, scaleToUse, roundingMode);

        } else if (d instanceof HugeDecimal) {

            return downscale(((HugeDecimal) d).unscaledValue, scale, scaleToUse, roundingMode);

        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
    }

    static Decimal descaleLong(long unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        if (scaleToUse >= scale) {
            return createDecimal(unscaledValue, scale);
        }

        return downscale(unscaledValue, scale, scaleToUse, roundingMode);
    }

    static Decimal descaleBigInteger(BigInteger unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        if (scaleToUse >= scale) {
            return createDecimal(unscaledValue, scale);
        }

        return downscale(unscaledValue, scale, scaleToUse, roundingMode);
    }


    private static Decimal downscale(long unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        long scaleDiff = (long) scale - scaleToUse;

        long valueWithRoundingDigit = downscaleByPowerOf10(unscaledValue, scaleDiff - 1);
        boolean hasAdditionalRemainder = unscaledValue != upscaleByPowerOf10(valueWithRoundingDigit, scaleDiff - 1);
        if (valueWithRoundingDigit == 0 && !hasAdditionalRemainder) {
            return Decimal.ZERO;
        }

        long rescaledValue = valueWithRoundingDigit / 10L;
        int remainingDigit = (int) (valueWithRoundingDigit - (rescaledValue * 10L));

        int roundingCorrection = DecimalRounder.roundingCorrection(
                Long.signum(unscaledValue),
                rescaledValue,
                remainingDigit,
                hasAdditionalRemainder,
                roundingMode
        );
        rescaledValue += roundingCorrection;

        return createDecimal(rescaledValue, scaleToUse);
    }

    private static Decimal downscale(BigInteger unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        // todo: check scale overflow
        int scaleDiff = scale - scaleToUse;

        boolean hasAdditionalRemainder = false;
        BigInteger valueWithRoundingDigit = unscaledValue;
        int n = scaleDiff - 1;
        // todo: simplify this
        while (n > 0 && valueWithRoundingDigit.signum() != 0) {
            int descaleBy = Math.min(PowerMath.maxBigPowerOf10(), n);
            BigInteger[] divAndMod = valueWithRoundingDigit.divideAndRemainder(PowerMath.powerOf10Big(descaleBy));
            valueWithRoundingDigit = divAndMod[0];
            if (divAndMod[1].signum() != 0) {
                hasAdditionalRemainder = true;
            }
            n -= descaleBy;
        }

        if (valueWithRoundingDigit.signum() == 0 && !hasAdditionalRemainder) {
            return Decimal.ZERO;
        }

        BigInteger[] divAndMod = valueWithRoundingDigit.divideAndRemainder(BIG_TEN);
        BigInteger rescaledValue = divAndMod[0];
        int remainingDigit = divAndMod[1].intValue();
        if (unscaledValue.signum() < 0 && remainingDigit > 0) {
            remainingDigit -= 10;
        }

        BigInteger roundingCorrection = DecimalRounder.roundingCorrection(
                unscaledValue.signum(),
                rescaledValue,
                remainingDigit,
                hasAdditionalRemainder,
                roundingMode
        );
        rescaledValue = rescaledValue.add(roundingCorrection);

        return createDecimal(rescaledValue, scaleToUse);
    }
}
