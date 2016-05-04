package mtymes.javafixes.beta.decimal;

import java.math.RoundingMode;

import static java.lang.Math.max;
import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.LongUtil.*;

// todo: test it
class DecimalAdder {

    // todo: IMPLEMENT PROPERLY
    // todo: IMPLEMENT PROPERLY
    // todo: IMPLEMENT PROPERLY
    // todo: IMPLEMENT PROPERLY
    // todo: IMPLEMENT PROPERLY
    // todo: HUGEDECIMAL

    // todo: add support for HugeDecimal
    static Decimal add(Decimal x, Decimal y, int scaleToUse, RoundingMode roundingMode) {

        int scaleX = x.scale();
        int scaleY = y.scale();
        // todo: this is easy to read, but maybe
        // todo: use rather min(scaleToUse, scaleX, scaleY) to actually prevent unnecessary overflow
        int maxScale = max(scaleX, scaleY);

        long maxScaledValueX = multiplyExact(x.unscaledValue(), pow10(maxScale - scaleX));
        long maxScaledValueY = multiplyExact(y.unscaledValue(), pow10(maxScale - scaleY));

        return createDecimal(
                addExact(maxScaledValueX, maxScaledValueY),
                maxScale
        ).descaleTo(scaleToUse, roundingMode);
    }

//    private Decimal add(int scaleA, int scaleB, BigInteger unscaledValueA, BigInteger unscaledValueB, int scaleToUse, RoundingMode roundingMode) {
//        int maxScale = max(scaleA, scaleB);
//
//        if (scaleA < maxScale) {
//            unscaledValueA = upscaleByPowerOf10(unscaledValueA, maxScale - scaleA);
//        }
//        if (scaleB < maxScale) {
//            unscaledValueB = upscaleByPowerOf10(unscaledValueB, maxScale - scaleB);
//        }
//
//        return createDecimal(
//                unscaledValueA.add(unscaledValueB),
//                maxScale
//        ).descaleTo(scaleToUse, roundingMode);
//    }
}
