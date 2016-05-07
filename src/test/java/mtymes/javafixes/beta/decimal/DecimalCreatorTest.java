package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import static mtymes.javafixes.test.CollectionUtil.removeFrom;
import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertThat;

public class DecimalCreatorTest {

    // todo: add test that the creator will strip trailing zeros

    @Test
    public void shouldBeAbleToCreateDecimalFromLong() {
//        long unscaledValue = Long.MIN_VALUE;
//        while(true) {
        for (int i = 0; i < 500_000; i++) {

            long unscaledValue = randomLong();
            int scale = randomInt(-1000, 1000);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);
            BigDecimal referenceValue = BigDecimal.valueOf(unscaledValue, scale);

            assertThat(decimal.bigDecimalValue(), comparesEqualTo(referenceValue));
        }
//            if (unscaledValue == Long.MAX_VALUE) {
//                break;
//            }
//            unscaledValue++;
//        }
    }

    @Test
    public void shouldBeAbleToCreateDecimalFromBigInteger() {
        for (int i = 0; i < 500_000; i++) {

            BigInteger unscaledValue = randomBigInteger();
            int scale = randomInt(-1000, 1000);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);
            BigDecimal referenceValue = new BigDecimal(unscaledValue, scale);

            assertThat(decimal.bigDecimalValue(), comparesEqualTo(referenceValue));
        }
    }

    // todo: my scaling is wrong as it takes into consideration just the last digit not all remaining digits
    @Test
    public void shouldBeAbleToCreateDecimalFromLongAndRescaleIt() {
        List<RoundingMode> usableRoundingModes = removeFrom(RoundingMode.values(), RoundingMode.UNNECESSARY);

        for (int i = 0; i < 500_000; i++) {

            long unscaledValue = randomLong();
            int scale = randomInt(-10, 10);
            int scaleToUse = randomInt(-10, 10);
            RoundingMode roundingMode = pickRandomValue(usableRoundingModes);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale, scaleToUse, roundingMode);
            BigDecimal bigDecimal = BigDecimal.valueOf(unscaledValue, scale).setScale(scaleToUse, roundingMode);

            int comparisonResult = decimal.bigDecimalValue().compareTo(bigDecimal);
            if (comparisonResult != 0) {
                throw new AssertionError("" +
                        "got different result for unscaledValue = " + unscaledValue + ", scale = " + scale + ", scaleToUse = " + scaleToUse + ", roundingMode = " + roundingMode +
                        "\n original   = " + new LongDecimal(unscaledValue, scale) +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + bigDecimal.toPlainString()
                );
            }
        }
    }

    // todo: my scaling is wrong as it takes into consideration just the last digit not all remaining digits
    @Test
    public void shouldBeAbleToCreateDecimalFromBigIntegerAndRescaleIt() {
        List<RoundingMode> usableRoundingModes = removeFrom(RoundingMode.values(), RoundingMode.UNNECESSARY);

        for (int i = 0; i < 500_000; i++) {

            BigInteger unscaledValue = randomBigInteger();
            int scale = randomInt(-10, 10);
            int scaleToUse = randomInt(-10, 10);
            RoundingMode roundingMode = pickRandomValue(usableRoundingModes);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale, scaleToUse, roundingMode);
            BigDecimal bigDecimal = new BigDecimal(unscaledValue, scale).setScale(scaleToUse, roundingMode);

            int comparisonResult = decimal.bigDecimalValue().compareTo(bigDecimal);
            if (comparisonResult != 0) {
                throw new AssertionError("" +
                        "got different result for unscaledValue = " + unscaledValue + ", scale = " + scale + ", scaleToUse = " + scaleToUse + ", roundingMode = " + roundingMode +
                        "\n original   = " + new HugeDecimal(unscaledValue, scale) +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + bigDecimal.toPlainString()
                );
            }
        }
    }
}