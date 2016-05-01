package co.uk.matejtymes.tdp;

import co.uk.matejtymes.tdp.Decimal.HugeDecimal;
import co.uk.matejtymes.tdp.Decimal.LongDecimal;
import org.apache.commons.lang.NotImplementedException;

import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.DecimalCreator.createDecimal;
import static co.uk.matejtymes.tdp.LongUtil.*;

// todo: test it
public class DecimalScaler {

    // todo: add support for HugeDecimal
    static Decimal descaleTo(Decimal d, int scaleToUse, RoundingMode roundingMode) {

        int scale = d.scale();
        if (scaleToUse >= scale) {
            return d; // no need to scale
        }

        int scaleDiff = scale - scaleToUse;

        if (d instanceof LongDecimal) {
            long unscaledValue = d.unscaledValue();

            long scaler = pow10(scaleDiff);
            long rescaledValue = unscaledValue / scaler;
            long remainder = subtractExact(unscaledValue, multiplyExact(rescaledValue, scaler));
            long remainingDigit = remainder / (scaler / 10);

            rescaledValue = DecimalsIntern.roundBasedOnRemainder(rescaledValue, remainingDigit, roundingMode);

            return createDecimal(rescaledValue, scaleToUse);
        } else if (d instanceof HugeDecimal) {
            // todo: implement
            throw new NotImplementedException("");
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }
    }
}
