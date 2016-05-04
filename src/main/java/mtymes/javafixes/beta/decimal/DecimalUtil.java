package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;

class DecimalUtil {

    static BigInteger bigUnscaledValue(Decimal d) {
        BigInteger unscaledValue;
        if (d instanceof Decimal.LongDecimal) {
            unscaledValue = BigInteger.valueOf(((Decimal.LongDecimal) d).unscaledValue);
        } else if (d instanceof Decimal.HugeDecimal) {
            unscaledValue = ((Decimal.HugeDecimal) d).unscaledValue;
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
        return unscaledValue;
    }
}
