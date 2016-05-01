package co.uk.matejtymes.tdp;

import co.uk.matejtymes.tdp.Decimal.HugeDecimal;
import co.uk.matejtymes.tdp.Decimal.LongDecimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.DecimalCreator.createDecimal;
import static co.uk.matejtymes.tdp.DecimalMath.*;
import static java.lang.Math.min;
import static java.lang.String.format;

// todo: test it
public class DecimalScaler {

    // todo: refactor to make it more readable

    static Decimal descaleTo(Decimal d, int scaleToUse, RoundingMode roundingMode) {

        int scale = d.scale();
        if (scaleToUse >= scale) {
            return d; // no need to scale
        }

        int scaleDiff = scale - scaleToUse;

        if (d instanceof LongDecimal) {
            long unscaledValue = ((LongDecimal) d).unscaledValue;

            while (scaleDiff > 1 && unscaledValue != 0) {
                int descalePower = min(maxLongPowerOf10(), scaleDiff - 1);
                unscaledValue /= powerOf10Long(descalePower);
                scaleDiff -= descalePower;
            }

            if (unscaledValue == 0) {
                return Decimal.ZERO;
            }

            long rescaledValue = unscaledValue / (long) 10;
            byte remainingDigit = (byte) (unscaledValue - (rescaledValue * 10));

            byte roundingCorrection = roundingCorrection(rescaledValue, remainingDigit, roundingMode);
            rescaledValue += roundingCorrection;

            return createDecimal(rescaledValue, scaleToUse);
        } else if (d instanceof HugeDecimal) {
            BigInteger unscaledValue = ((HugeDecimal) d).unscaledValue;

            while (scaleDiff > 1 && unscaledValue.signum() != 0) {
                int descalePower = min(maxBigPowerOf10(), scaleDiff - 1);
                unscaledValue = unscaledValue.divide(powerOf10Big(descalePower));
                scaleDiff -= descalePower;
            }

            if (unscaledValue.signum() == 0) {
                return Decimal.ZERO;
            }

            BigInteger rescaledValue = unscaledValue.divide(BigInteger.TEN);
            byte remainingDigit = unscaledValue.mod(BigInteger.TEN).byteValue();
            if (unscaledValue.signum() < 0) {
                remainingDigit -= 10;
            }

            BigInteger roundingCorrection = roundingCorrection(rescaledValue, remainingDigit, roundingMode);
            rescaledValue = rescaledValue.add(roundingCorrection);

            return createDecimal(rescaledValue, scaleToUse);
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
    }

    // todo: move this code somewhere else Decimal Rounder


    // todo: in the future make sure the digit is only from 0 to 9 (currently the sign of the digit makes it a little bit awkward)
    private static byte roundingCorrection(long valueBeforeRounding, byte remainingDigit, RoundingMode roundingMode) {
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

    private static final BigInteger TWO_BIG = BigInteger.valueOf(2L);
    private static final BigInteger BIG_ONE = BigInteger.ONE;
    private static final BigInteger BIG_MINUS_ONE = BigInteger.ONE.negate();

    // todo: in the future make sure the digit is only from 0 to 9 (currently the sign of the digit makes it a little bit awkward)
    private static BigInteger roundingCorrection(BigInteger valueBeforeRounding, byte remainingDigit, RoundingMode roundingMode) {
        if (remainingDigit < -9 || remainingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid remaining digit (%d). Should be only -9 to 9", remainingDigit));
        }

        BigInteger roundingCorrection = BigInteger.ZERO;

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
                    if (remainingDigit > 5 || (remainingDigit == 5 && valueBeforeRounding.mod(TWO_BIG).signum() != 0)) {
                        roundingCorrection = BIG_ONE;
                    }
                } else {
                    if (remainingDigit < -5 || (remainingDigit == -5 && valueBeforeRounding.mod(TWO_BIG).signum() != 0)) {
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
