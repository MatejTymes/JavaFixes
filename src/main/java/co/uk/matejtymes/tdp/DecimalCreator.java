package co.uk.matejtymes.tdp;

import java.math.BigInteger;

import static co.uk.matejtymes.tdp.BigIntegerUtil.canConvertToLong;
import static java.math.BigInteger.TEN;

// todo: test it
class DecimalCreator {

    static Decimal createDecimal(long unscaledValue, int scale) {
        // todo: strip trailing zeros in better way
        while (unscaledValue != 0 && unscaledValue % 10 == 0) {
            unscaledValue /= 10;
            scale--;
        }

        return new Decimal.LongDecimal(unscaledValue, scale);
    }

    static Decimal createDecimal(BigInteger unscaledValue, int scale) {
        // todo: strip trailing zeros in better way
        while (unscaledValue.signum() != 0 && unscaledValue.mod(TEN).signum() == 0) {
            unscaledValue = unscaledValue.divide(TEN);
            scale--;
        }

        if (canConvertToLong(unscaledValue)) {
            return new Decimal.LongDecimal(unscaledValue.longValue(), scale);
        } else {
            return new Decimal.HugeDecimal(unscaledValue, scale);
        }
    }
}
