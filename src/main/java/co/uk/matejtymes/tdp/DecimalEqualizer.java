package co.uk.matejtymes.tdp;

import co.uk.matejtymes.tdp.Decimal.HugeDecimal;
import co.uk.matejtymes.tdp.Decimal.LongDecimal;

import java.math.BigInteger;

import static co.uk.matejtymes.tdp.DecimalMath.maxBigPowerOf10;
import static co.uk.matejtymes.tdp.DecimalMath.powerOf10Big;
import static co.uk.matejtymes.tdp.LongUtil.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

// todo: test it
public class DecimalEqualizer {

    // todo: move equals and hashCode methods as well

    static int compare(Decimal x, Decimal y) {

        int signComparison = Integer.compare(x.signum(), y.signum());
        if (signComparison != 0) {
            return signComparison;
        }

        int scaleX = x.scale();
        int scaleY = y.scale();

        if (x instanceof LongDecimal && y instanceof LongDecimal) {
            long unscaledX = x.unscaledValue();
            long unscaledY = y.unscaledValue();

            if (scaleX == scaleY) {
                return Long.compare(unscaledX, unscaledY);
            }

            // todo: what if the difference is massive

            // todo: reimplement this - might get an overflow
            int minScale = min(scaleX, scaleY);

            long scalerX = pow10(scaleX - minScale);
            long scalerY = pow10(scaleY - minScale);

            // by doing division instead of multiplication we prevent overflow
            long xScaledDownValue = unscaledX / scalerX;
            long yScaledDownValue = unscaledY / scalerY;

            int comparison = Long.compare(xScaledDownValue, yScaledDownValue);
            if (comparison != 0) {
                return comparison;
            } else {
                long xRemainder = subtractExact(unscaledX, multiplyExact(xScaledDownValue, scalerX));
                long yRemainder = subtractExact(unscaledY, multiplyExact(yScaledDownValue, scalerY));

                return Long.compare(xRemainder, yRemainder);
            }
        } else {
            BigInteger unscaledX = bigUnscaledValue(x);
            BigInteger unscaledY = bigUnscaledValue(y);

            int maxScale = max(scaleX, scaleY);

            while(scaleX < maxScale) {
                int upscalePower = min(maxBigPowerOf10(), maxScale - scaleX);
                unscaledX = unscaledX.multiply(powerOf10Big(upscalePower));
                scaleX += upscalePower;
            }

            while(scaleY < maxScale) {
                int upscalePower = min(maxBigPowerOf10(), maxScale - scaleY);
                unscaledY = unscaledY.multiply(powerOf10Big(upscalePower));
                scaleY += upscalePower;
            }

            return unscaledX.compareTo(unscaledY);
        }
    }

    private static BigInteger bigUnscaledValue(Decimal d) {
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
