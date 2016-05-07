package mtymes.javafixes.beta.decimal;

import java.math.BigInteger;
import java.math.RoundingMode;

import static mtymes.javafixes.beta.decimal.DecimalCreator.createDecimal;
import static mtymes.javafixes.beta.decimal.DecimalUtil.bigUnscaledValueFrom;

// todo: test it
class DecimalMultiplier {

    static Decimal multiply(Decimal a, Decimal b, int scaleToUse, RoundingMode roundingMode) {

        // todo: although correct (no overflows) currently slow when handling just longs - refactor in the near future
        BigInteger unscaledValueA = bigUnscaledValueFrom(a);
        BigInteger unscaledValueB = bigUnscaledValueFrom(b);

        return createDecimal(
                unscaledValueA.multiply(unscaledValueB),
                a.scale() + b.scale(),
                scaleToUse,
                roundingMode
        );
    }
}
