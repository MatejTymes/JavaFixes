package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.String.format;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.DecimalMath.*;

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

        unscaledValue = downscaleByPowerOf10(unscaledValue, scaleDiff - 1);

        if (unscaledValue == 0) {
            return Decimal.ZERO;
        }

        long rescaledValue = unscaledValue / (long) 10;
        byte remainingDigit = (byte) (unscaledValue - (rescaledValue * 10));

        byte roundingCorrection = roundingCorrection(rescaledValue, remainingDigit, roundingMode);
        rescaledValue += roundingCorrection;

        return createDecimal(rescaledValue, scaleToUse);
    }

    private static Decimal downscale(BigInteger unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
        int scaleDiff = scale - scaleToUse;

        unscaledValue = downscaleByPowerOf10(unscaledValue, scaleDiff - 1);

        if (unscaledValue.signum() == 0) {
            return Decimal.ZERO;
        }

        BigInteger rescaledValue = unscaledValue.divide(BIG_TEN);
        byte remainingDigit = unscaledValue.mod(BIG_TEN).byteValue();
        if (unscaledValue.signum() < 0) {
            remainingDigit -= 10;
        }

        BigInteger roundingCorrection = roundingCorrection(rescaledValue, remainingDigit, roundingMode);
        rescaledValue = rescaledValue.add(roundingCorrection);

        return createDecimal(rescaledValue, scaleToUse);
    }

    // todo: move this code somewhere else Decimal Rounder


    // todo: in the future make sure the digit is only from 0 to 9 (currently the sign of the digit makes it a little bit awkward)
    static byte roundingCorrection(long valueBeforeRounding, byte remainingDigit, RoundingMode roundingMode) {
        if (remainingDigit < -9 || remainingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid remaining digit (%d). Should be only -9 to 9", remainingDigit));
        }

        byte roundingCorrection = 0;

        if (remainingDigit != 0) {
            if (roundingMode == RoundingMode.UP) {
                if (remainingDigit > 0 && valueBeforeRounding >= 0) {
                    roundingCorrection = 1;
                } else if (remainingDigit < 0 && valueBeforeRounding < 0) {
                    roundingCorrection = -1;
                }
            } else if (roundingMode == RoundingMode.DOWN) {
                if (remainingDigit < 0 && valueBeforeRounding >= 0) {
                    roundingCorrection = -1;
                } else if (remainingDigit > 0 && valueBeforeRounding < 0) {
                    roundingCorrection = 1;
                }
            } else if (roundingMode == RoundingMode.CEILING) {
                if (remainingDigit > 0 && valueBeforeRounding >= 0) {
                    roundingCorrection = 1;
                }
            } else if (roundingMode == RoundingMode.FLOOR) {
                if (remainingDigit < 0 && valueBeforeRounding < 0) {
                    roundingCorrection = -1;
                }
            } else if (roundingMode == RoundingMode.HALF_UP) {
                if (remainingDigit >= 5 && valueBeforeRounding >= 0) {
                    roundingCorrection = 1;
                } else if (remainingDigit <= -5 && valueBeforeRounding < 0) {
                    roundingCorrection = -1;
                }
            } else if (roundingMode == RoundingMode.HALF_DOWN) {
                if (remainingDigit > 5 && valueBeforeRounding >= 0) {
                    roundingCorrection = 1;
                } else if (remainingDigit < -5 && valueBeforeRounding < 0) {
                    roundingCorrection = -1;
                }
            } else if (roundingMode == RoundingMode.HALF_EVEN) {
                if (valueBeforeRounding >= 0) {
                    if (remainingDigit > 5 || (remainingDigit == 5 && valueBeforeRounding % 2 != 0)) {
                        roundingCorrection = 1;
                    }
                } else {
                    if (remainingDigit < -5 || (remainingDigit == -5 && valueBeforeRounding % 2 != 0)) {
                        roundingCorrection = -1;
                    }
                }
            } else if (roundingMode == RoundingMode.UNNECESSARY) {
                throw new ArithmeticException("Rounding necessary");
            }
        }

        return roundingCorrection;
    }

    // todo: in the future make sure the digit is only from 0 to 9 (currently the sign of the digit makes it a little bit awkward)
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
