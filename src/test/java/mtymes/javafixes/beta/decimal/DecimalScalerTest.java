package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import static mtymes.javafixes.beta.decimal.Decimal.decimal;
import static mtymes.javafixes.test.CollectionUtil.removeFrom;
import static mtymes.javafixes.test.Random.*;

public class DecimalScalerTest {

    @Test
    public void shouldBeAbleToDescaleDecimal() {
        List<RoundingMode> usableRoundingModes = removeFrom(RoundingMode.values(), RoundingMode.UNNECESSARY);

        for (int i = 0; i < 500_000; i++) {

            Decimal original = decimal(randomBigDecimal());
            int scaleToUse = randomInt(-10, 10);
            RoundingMode roundingMode = pickRandomValue(usableRoundingModes);

            Decimal decimal = DecimalScaler.descaleTo(original, scaleToUse, roundingMode);
            BigDecimal referenceValue = original.bigDecimalValue().setScale(scaleToUse, roundingMode);

            int comparisonResult = decimal.bigDecimalValue().compareTo(referenceValue);
            if (comparisonResult != 0) {
                throw new AssertionError("" +
                        "got different result for value = " + original + ", scaleToUse = " + scaleToUse + ", roundingMode = " + roundingMode +
                        "\n original   = " + original +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + referenceValue.toPlainString()
                );
            }
        }
    }

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
                        "\n original   = " + new Decimal.LongDecimal(unscaledValue, scale) +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + referenceValue.toPlainString()
                );
            }
        }
    }

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
                        "\n original   = " + new Decimal.HugeDecimal(unscaledValue, scale) +
                        "\n decimal    = " + decimal.toPlainString() +
                        "\n bigDecimal = " + referenceValue.toPlainString()
                );
            }
        }
    }

}