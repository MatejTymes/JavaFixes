package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;

import static mtymes.javafixes.beta.decimal.BigIntegerUtil.canConvertToLong;
import static mtymes.javafixes.beta.decimal.DecimalMath.BIG_TEN;

// todo: test it
class DecimalCreator {

    static Decimal createDecimal(long unscaledValue, int scale) {
        // todo: strip trailing zeros in better/faster way
        while (unscaledValue != 0 && unscaledValue % 10 == 0) {
            unscaledValue /= 10;
            scale--;
        }

        return new Decimal.LongDecimal(unscaledValue, scale);
    }

    static Decimal createDecimal(BigInteger unscaledValue, int scale) {
        // todo: strip trailing zeros in better/faster way
        while (unscaledValue.signum() != 0 && unscaledValue.mod(BIG_TEN).signum() == 0) {
            unscaledValue = unscaledValue.divide(BIG_TEN);
            scale--;
        }

        if (canConvertToLong(unscaledValue)) {
            return new Decimal.LongDecimal(unscaledValue.longValue(), scale);
        } else {
            return new Decimal.HugeDecimal(unscaledValue, scale);
        }
    }
}
