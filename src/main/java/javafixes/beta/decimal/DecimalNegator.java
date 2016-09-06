package javafixes.beta.decimal;

import java.math.BigInteger;

import static javafixes.beta.decimal.DecimalCreator.createDecimal;

class DecimalNegator {

    static Decimal negate(Decimal d) {
        if (d instanceof Decimal.HugeDecimal) {
            return createDecimal(((Decimal.HugeDecimal) d).unscaledValue.negate(), d.scale());
        } else if (d instanceof Decimal.LongDecimal) {
            long unscaledValue = ((Decimal.LongDecimal) d).unscaledValue;
            if (unscaledValue == Long.MIN_VALUE) {
                return createDecimal(BigInteger.valueOf(unscaledValue).negate(), d.scale());
            } else {
                return createDecimal(-unscaledValue, d.scale());
            }
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
    }
}
