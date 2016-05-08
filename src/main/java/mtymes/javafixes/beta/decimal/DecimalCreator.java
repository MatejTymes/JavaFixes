package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static mtymes.javafixes.beta.decimal.BigIntegerUtil.canConvertToLong;
import static mtymes.javafixes.beta.decimal.Constants.BIG_TEN;

// todo: maybe merge DecimalCreator and DecimalScaler
class DecimalCreator {

    // todo: add flag to not remove trailing zeros
    // - this can resolve into higher memory consumption but might increase creation times and some arithmetic operations

    static Decimal createDecimal(long unscaledValue, int scale) {
        while (unscaledValue != 0L
                && ((int) unscaledValue & 1) == 0
                && unscaledValue % 10 == 0) {
            unscaledValue /= 10L;
            scale--;
        }

        return new Decimal.LongDecimal(unscaledValue, scale);
    }

    static Decimal createDecimal(BigInteger unscaledValue, int scale) {
        while (unscaledValue.signum() != 0) {
            BigInteger[] divAndMod = unscaledValue.divideAndRemainder(BIG_TEN);
            if (divAndMod[1].signum() != 0) {
                break;
            }
            unscaledValue = divAndMod[0];
            scale--;
        }

        if (canConvertToLong(unscaledValue)) {
            return new Decimal.LongDecimal(unscaledValue.longValue(), scale);
        } else {
            return new Decimal.HugeDecimal(unscaledValue, scale);
        }
    }

    static Decimal createDecimal(long unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
//        return createDecimal(unscaledValue, scale).descaleTo(scaleToUse, roundingMode);
        return DecimalScaler.descaleTo(unscaledValue, scale, scaleToUse, roundingMode);
    }

    static Decimal createDecimal(BigInteger unscaledValue, int scale, int scaleToUse, RoundingMode roundingMode) {
//        return createDecimal(unscaledValue, scale).descaleTo(scaleToUse, roundingMode);
        return DecimalScaler.descaleTo(unscaledValue, scale, scaleToUse, roundingMode);
    }
}
