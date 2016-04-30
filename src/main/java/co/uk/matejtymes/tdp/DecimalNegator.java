package co.uk.matejtymes.tdp;

import java.math.BigInteger;

import static co.uk.matejtymes.tdp.DecimalCreator.toDecimal;

// todo: testit
class DecimalNegator {

    static Decimal negate(Decimal d) {
        if (d instanceof Decimal.HugeDecimal) {
            return toDecimal(((Decimal.HugeDecimal) d).unscaledValue.negate(), d.scale());
        } else if (d instanceof Decimal.LongDecimal) {
            long unscaledValue = ((Decimal.LongDecimal) d).unscaledValue;
            if (unscaledValue == Long.MIN_VALUE) {
                return toDecimal(BigInteger.valueOf(unscaledValue).negate(), d.scale());
            } else {
                return toDecimal(-unscaledValue, d.scale());
            }
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
    }
}
