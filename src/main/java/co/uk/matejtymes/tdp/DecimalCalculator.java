package co.uk.matejtymes.tdp;

import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.Decimal.decimal;
import static co.uk.matejtymes.tdp.LongUtil.*;
import static co.uk.matejtymes.tdp.RoundUtil.roundBasedOnRemainder;
import static java.lang.Math.max;

// todo: test this
public class DecimalCalculator {

    // todo: test
    public static Decimal add(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        int scaleX = x.getScale();
        int scaleY = y.getScale();
        // todo: this is easy to read, but maybe
        // todo: use rather min(scaleToUse, scaleX, scaleY) to actually prevent unnecessary overflow
        int maxScale = max(scaleX, scaleY);

        long maxScaledValueX = multiplyExact(x.getUnscaledValue(), pow10(maxScale - scaleX));
        long maxScaledValueY = multiplyExact(y.getUnscaledValue(), pow10(maxScale - scaleY));

        return decimal(
                addExact(maxScaledValueX, maxScaledValueY),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    public static Decimal subtract(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        int scaleX = x.getScale();
        int scaleY = y.getScale();
        // todo: this is easy to read, but maybe
        // todo: use rather min(scaleToUse, scaleX, scaleY) to actually prevent unnecessary overflow
        int maxScale = max(scaleX, scaleY);

        long maxScaledValueX = multiplyExact(x.getUnscaledValue(), pow10(maxScale - scaleX));
        long maxScaledValueY = multiplyExact(y.getUnscaledValue(), pow10(maxScale - scaleY));

        return decimal(
                subtractExact(maxScaledValueX, maxScaledValueY),
                maxScale
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    public static Decimal multiply(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {
        Decimal strippedX = x.stripTrailingZeros();
        Decimal strippedY = y.stripTrailingZeros();

        return decimal(
                // todo: extremely simplistic algorithm - please improve
                // todo: - this is extremely prone to overflow
                multiplyExact(strippedX.getUnscaledValue(), strippedY.getUnscaledValue()),
                strippedX.getScale() + strippedY.getScale()
        ).rescaleTo(scaleToUse, roundingMode);
    }

    // todo: test
    // todo: handle null pointer exception
    public static Decimal divide(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {

        long unscaledX = x.getUnscaledValue();
        long unscaledY = y.getUnscaledValue();

        boolean positive = (unscaledX < 0 && unscaledY < 0) ||
                (unscaledX >= 0 && unscaledY >= 0);

        long remainder = absExact(unscaledX);
        long divisor = absExact(unscaledY);

        long result = remainder / divisor;
        remainder = multiplyExact((remainder % divisor), 10);
        int newScale = x.getScale() - y.getScale();

        if (newScale > scaleToUse) {
            // todo: test this
            if (!positive) {
                result = negateExact(result);
            }
            return decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        } else {
            while (newScale < scaleToUse) {
                result = addExact(multiplyExact(result, 10), (remainder / divisor));
                remainder = multiplyExact((remainder % divisor), 10);
                newScale++;
            }
            long remainingDigit = remainder / divisor;
            if (!positive) {
                result = negateExact(result);
                remainingDigit = -remainingDigit;
            }
            result = roundBasedOnRemainder(result, remainingDigit, roundingMode);


            return decimal(result, newScale)
                    .rescaleTo(scaleToUse, roundingMode);
        }
    }
}
