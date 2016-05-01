package co.uk.matejtymes.tdp;

import co.uk.matejtymes.tdp.Decimal.HugeDecimal;
import co.uk.matejtymes.tdp.Decimal.LongDecimal;

import java.math.BigInteger;

import static co.uk.matejtymes.tdp.DecimalMath.*;
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

            int scaleToGet = min(scaleX, scaleY);

            long rescaledX = descaleValue(unscaledX, scaleX, scaleToGet);
            long rescaledY = descaleValue(unscaledY, scaleY, scaleToGet);

            int topComparison = Long.compare(rescaledX, rescaledY);
            if (topComparison != 0) {
                return topComparison;
            } else {
                long remainderX = unscaledX - upScaleValue(rescaledX, scaleToGet, scaleX);
                long remainderY = unscaledY - upScaleValue(rescaledY, scaleToGet, scaleY);

                return Long.compare(remainderX, remainderY);
            }
        } else {
            BigInteger unscaledX = bigUnscaledValue(x);
            BigInteger unscaledY = bigUnscaledValue(y);

            int maxScale = max(scaleX, scaleY);

            while (scaleX < maxScale) {
                int upscalePower = min(maxBigPowerOf10(), maxScale - scaleX);
                unscaledX = unscaledX.multiply(powerOf10Big(upscalePower));
                scaleX += upscalePower;
            }

            while (scaleY < maxScale) {
                int upscalePower = min(maxBigPowerOf10(), maxScale - scaleY);
                unscaledY = unscaledY.multiply(powerOf10Big(upscalePower));
                scaleY += upscalePower;
            }

            return unscaledX.compareTo(unscaledY);
        }
    }

    private static long descaleValue(long value, int fromScale, int toScale) {
        long rescaledValue = value;
        int newScale = fromScale;

        while (rescaledValue != 0 && toScale < newScale) {
            int scale = min(maxLongPowerOf10(), newScale - toScale);
            long scaler = powerOf10Long(scale);
            rescaledValue /= scaler;
            newScale -= scale;
        }

        return rescaledValue;
    }

    private static long upScaleValue(long value, int fromScale, int toScale) {
        long rescaledValue = value;
        int newScale = fromScale;

        while (rescaledValue != 0 && toScale > newScale) {
            int scale = min(maxLongPowerOf10(), toScale - newScale);
            long scaler = powerOf10Long(scale);
            rescaledValue *= scaler;
            newScale += scale;
        }

        return rescaledValue;
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
