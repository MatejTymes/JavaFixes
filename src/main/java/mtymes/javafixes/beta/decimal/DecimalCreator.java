package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;

import static mtymes.javafixes.beta.decimal.BigIntegerUtil.canConvertToLong;
import static mtymes.javafixes.beta.decimal.Constants.BIG_TEN;

class DecimalCreator {

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
}
