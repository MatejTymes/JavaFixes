package co.uk.matejtymes.tdp;

import static co.uk.matejtymes.tdp.LongUtil.*;
import static java.lang.Math.min;

// todo: test it
public class DecimalEqualizer {

    // todo: move equals and hashCode methods as well

    // todo: add support for HugeDecimal
    static int compare(Decimal x, Decimal y) {
        int scaleX = x.scale();
        int scaleY = y.scale();

        long unscaledX = x.unscaledValue();
        long unscaledY = y.unscaledValue();

        if (scaleX == scaleY) {
            return Long.compare(unscaledX, unscaledY);
        }

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
    }
}
