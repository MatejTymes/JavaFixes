package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;

// todo: test this
class DecimalUtil {

    static BigInteger bigUnscaledValueFrom(Decimal d) {
        BigInteger unscaledValue;
        if (d instanceof LongDecimal) {
            unscaledValue = BigInteger.valueOf(((LongDecimal) d).unscaledValue);
        } else if (d instanceof HugeDecimal) {
            unscaledValue = ((HugeDecimal) d).unscaledValue;
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
        return unscaledValue;
    }
}
