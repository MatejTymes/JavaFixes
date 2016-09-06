package javafixes.beta.decimal;

import javafixes.beta.decimal.Decimal.HugeDecimal;
import javafixes.beta.decimal.Decimal.LongDecimal;

import java.math.BigInteger;

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
