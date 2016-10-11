package javafixes.beta.decimal;

import java.math.BigInteger;

import static javafixes.beta.decimal.Constants.BIG_TEN;
import static javafixes.math.util.BigIntegerUtil.canConvertToLong;

// todo: make part of the Decimal
class DecimalCreator {

    static Decimal createDecimal(long unscaledValue, int scale) {
        while (unscaledValue != 0
                && ((int) unscaledValue & 1) == 0
                && unscaledValue % 10 == 0) {
            unscaledValue /= 10;

            // todo: test this
            if (scale == Integer.MIN_VALUE) {
                throw new ArithmeticException("Scale overflow - can't set scale to less than: " + Integer.MIN_VALUE);
            }
            scale--;
        }

        return new Decimal.LongDecimal(unscaledValue, unscaledValue == 0 ? 0 : scale);
    }

    static Decimal createDecimal(BigInteger unscaledValue, int scale) {
        while (unscaledValue.signum() != 0) {
            BigInteger[] divAndMod = unscaledValue.divideAndRemainder(BIG_TEN);
            if (divAndMod[1].signum() != 0) {
                break;
            }
            unscaledValue = divAndMod[0];

            // todo: test this
            if (scale == Integer.MIN_VALUE) {
                throw new ArithmeticException("Scale overflow - can't set scale to less than: " + Integer.MIN_VALUE);
            }
            scale--;
        }

        if (canConvertToLong(unscaledValue)) {
            long longUnscaledValue = unscaledValue.longValue();
            return new Decimal.LongDecimal(longUnscaledValue, longUnscaledValue == 0 ? 0 : scale);
        } else {
            return new Decimal.HugeDecimal(unscaledValue, unscaledValue.signum() == 0 ? 0 : scale);
        }
    }
}
