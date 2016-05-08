package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.String.format;
import static mtymes.javafixes.beta.decimal.Constants.*;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.PowerMath.downscaleByPowerOf10;
import static mtymes.javafixes.beta.decimal.PowerMath.upscaleByPowerOf10;

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

    static Decimal descaleTo(long unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        if (scaleToUse >= scale) {
            return createDecimal(unscaledValue, scale);
        }

        return downscale(unscaledValue, scale, scaleToUse, roundingMode);
    }

    static Decimal descaleTo(BigInteger unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        if (scaleToUse >= scale) {
            return createDecimal(unscaledValue, scale);
        }

        return downscale(unscaledValue, scale, scaleToUse, roundingMode);
    }


    private static Decimal downscale(long unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        int scaleDiff = scale - scaleToUse;

        long valueWithRoundingDigit = downscaleByPowerOf10(unscaledValue, scaleDiff - 1);
        boolean hasAdditionalRemainder = unscaledValue != upscaleByPowerOf10(valueWithRoundingDigit, scaleDiff - 1);
        if (valueWithRoundingDigit == 0 && !hasAdditionalRemainder) {
            return Decimal.ZERO;
        }

        long rescaledValue = valueWithRoundingDigit / (long) 10;
        int remainingDigit = (int) (valueWithRoundingDigit - (rescaledValue * 10));

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
        int scaleDiff = scale - scaleToUse;

        boolean hasAdditionalRemainder = false;
        BigInteger valueWithRoundingDigit = unscaledValue;
        int n = scaleDiff - 1;
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

        int roundingCorrection = DecimalRounder.roundingCorrection(
                unscaledValue.signum(),
                rescaledValue,
                remainingDigit,
                hasAdditionalRemainder,
                roundingMode
        );

        rescaledValue = rescaledValue.add(
                (roundingCorrection == 1)
                        ? BIG_ONE
                        : (roundingCorrection == -1)
                        ? BIG_MINUS_ONE
                        : BIG_ZERO
        );

        return createDecimal(rescaledValue, scaleToUse);
    }

    @Deprecated // todo: remove this - use DecimalRounder.roundingCorrection(...)
    static BigInteger roundingCorrection(BigInteger valueBeforeRounding, byte remainingDigit, RoundingMode roundingMode) {
        if (remainingDigit < -9 || remainingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid remaining digit (%d). Should be only -9 to 9", remainingDigit));
        }

        BigInteger roundingCorrection = BIG_ZERO;

        if (remainingDigit != 0) {
            if (roundingMode == RoundingMode.UP) {
                if (remainingDigit > 0 && valueBeforeRounding.signum() >= 0) {
                    roundingCorrection = BIG_ONE;
                } else if (remainingDigit < 0 && valueBeforeRounding.signum() < 0) {
                    roundingCorrection = BIG_MINUS_ONE;
                }
            } else if (roundingMode == RoundingMode.DOWN) {
                if (remainingDigit < 0 && valueBeforeRounding.signum() >= 0) {
                    roundingCorrection = BIG_MINUS_ONE;
                } else if (remainingDigit > 0 && valueBeforeRounding.signum() < 0) {
                    roundingCorrection = BIG_ONE;
                }
            } else if (roundingMode == RoundingMode.CEILING) {
                if (remainingDigit > 0 && valueBeforeRounding.signum() >= 0) {
                    roundingCorrection = BIG_ONE;
                }
            } else if (roundingMode == RoundingMode.FLOOR) {
                if (remainingDigit < 0 && valueBeforeRounding.signum() < 0) {
                    roundingCorrection = BIG_MINUS_ONE;
                }
            } else if (roundingMode == RoundingMode.HALF_UP) {
                if (remainingDigit >= 5 && valueBeforeRounding.signum() >= 0) {
                    roundingCorrection = BIG_ONE;
                } else if (remainingDigit <= -5 && valueBeforeRounding.signum() < 0) {
                    roundingCorrection = BIG_MINUS_ONE;
                }
            } else if (roundingMode == RoundingMode.HALF_DOWN) {
                if (remainingDigit > 5 && valueBeforeRounding.signum() >= 0) {
                    roundingCorrection = BIG_ONE;
                } else if (remainingDigit < -5 && valueBeforeRounding.signum() < 0) {
                    roundingCorrection = BIG_MINUS_ONE;
                }
            } else if (roundingMode == RoundingMode.HALF_EVEN) {
                if (valueBeforeRounding.signum() >= 0) {
                    if (remainingDigit > 5 || (remainingDigit == 5 && valueBeforeRounding.mod(BIG_TWO).signum() != 0)) {
                        roundingCorrection = BIG_ONE;
                    }
                } else {
                    if (remainingDigit < -5 || (remainingDigit == -5 && valueBeforeRounding.mod(BIG_TWO).signum() != 0)) {
                        roundingCorrection = BIG_MINUS_ONE;
                    }
                }
            } else if (roundingMode == RoundingMode.UNNECESSARY) {
                throw new ArithmeticException("Rounding necessary");
            }
        }

        return roundingCorrection;
    }
}
