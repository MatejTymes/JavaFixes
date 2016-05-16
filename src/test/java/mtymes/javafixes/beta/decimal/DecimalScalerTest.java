package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import static mtymes.javafixes.beta.decimal.Decimal.decimal;
import static mtymes.javafixes.test.CollectionUtil.removeFrom;
import static mtymes.javafixes.test.Random.*;

public class DecimalScalerTest {

    // add custom proper test

    // todo: move this into reference test
    @Test
    public void shouldBeAbleToDescaleDecimal() {
        List<RoundingMode> usableRoundingModes = removeFrom(RoundingMode.values(), RoundingMode.UNNECESSARY);

        for (int i = 0; i < 500_000; i++) {

            BigDecimal bigDecimal = randomBigDecimal();
            Decimal decimal = decimal(bigDecimal);
            int scaleToUse = randomInt(-10, 10);
            RoundingMode roundingMode = pickRandomValue(usableRoundingModes);

            Decimal decimalResult = DecimalScaler.descaleTo(decimal, scaleToUse, roundingMode);
            BigDecimal bigDecimalResult = bigDecimal.setScale(scaleToUse, roundingMode);

            int comparisonResult = decimalResult.bigDecimalValue().compareTo(bigDecimalResult);
            if (comparisonResult != 0) {
                throw new AssertionError("" +
                        "got different result for value = " + decimal + ", scaleToUse = " + scaleToUse + ", roundingMode = " + roundingMode +
                        "\n original         = " + decimal +
                        "\n decimalResult    = " + decimalResult.toPlainString() +
                        "\n bigDecimalResult = " + bigDecimalResult.toPlainString()
                );
            }
        }
    }

    // todo: move this into reference test
    @Test
    public void shouldBeAbleToDescaleLongUnscaledValue() {
        List<RoundingMode> usableRoundingModes = removeFrom(RoundingMode.values(), RoundingMode.UNNECESSARY);

        for (int i = 0; i < 500_000; i++) {

            long unscaledValue = randomLong();
            int scale = randomInt(-10, 10);
            int scaleToUse = randomInt(-10, 10);
            RoundingMode roundingMode = pickRandomValue(usableRoundingModes);

            Decimal decimal = DecimalScaler.descaleLong(unscaledValue, scale, scaleToUse, roundingMode);
            BigDecimal referenceValue = BigDecimal.valueOf(unscaledValue, scale).setScale(scaleToUse, roundingMode);

            int comparisonResult = decimal.bigDecimalValue().compareTo(referenceValue);
            if (comparisonResult != 0) {
                throw new AssertionError("" +
                        "got different result for unscaledValue = " + unscaledValue + ", scale = " + scale + ", scaleToUse = " + scaleToUse + ", roundingMode = " + roundingMode +
                        "\n original   = " + new LongDecimal(unscaledValue, scale) +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + referenceValue.toPlainString()
                );
            }
        }
    }

    // todo: move this into reference test
    @Test
    public void shouldBeAbleToDescaleBigIntegerUnscaledValue() {
        List<RoundingMode> usableRoundingModes = removeFrom(RoundingMode.values(), RoundingMode.UNNECESSARY);

        for (int i = 0; i < 500_000; i++) {

            BigInteger unscaledValue = randomBigInteger();
            int scale = randomInt(-10, 10);
            int scaleToUse = randomInt(-10, 10);
            RoundingMode roundingMode = pickRandomValue(usableRoundingModes);

            Decimal decimal = DecimalScaler.descaleBigInteger(unscaledValue, scale, scaleToUse, roundingMode);
            BigDecimal referenceValue = new BigDecimal(unscaledValue, scale).setScale(scaleToUse, roundingMode);

            int comparisonResult = decimal.bigDecimalValue().compareTo(referenceValue);
            if (comparisonResult != 0) {
                throw new AssertionError("" +
                        "got different result for unscaledValue = " + unscaledValue + ", scale = " + scale + ", scaleToUse = " + scaleToUse + ", roundingMode = " + roundingMode +
                        "\n original   = " + new HugeDecimal(unscaledValue, scale) +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + referenceValue.toPlainString()
                );
            }
        }
    }

}