package javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import static javafixes.beta.decimal.Decimal.decimal;
import static javafixes.test.CollectionUtil.removeFrom;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertThat;

public class BigDecimalReferenceTest {

    @Test
    public void shouldCreateValueFromLong() {
        for (int i = 0; i < 50_000; i++) {

            long unscaledValue = randomLong();
            int scale = randomInt(-1000, 1000);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);
            BigDecimal referenceValue = BigDecimal.valueOf(unscaledValue, scale);

            assertThat(decimal.bigDecimalValue(), comparesEqualTo(referenceValue));
        }
    }

    @Test
    public void shouldCreateValueFromBigInteger() {
        for (int i = 0; i < 50_000; i++) {

            BigInteger unscaledValue = randomBigInteger();
            int scale = randomInt(-1000, 1000);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);
            BigDecimal referenceValue = new BigDecimal(unscaledValue, scale);

            assertThat(decimal.bigDecimalValue(), comparesEqualTo(referenceValue));
        }
    }

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

    @Test
    public void shouldAddValue() {
        for (int i = 0; i < 25_000; i++) {

            Decimal decimalA;
            Decimal decimalB;
            BigDecimal referenceA;
            BigDecimal referenceB;

            int scaleA = randomInt(-20, 20);
            if (randomBoolean()) {
                long value = randomLong();
                decimalA = Decimal.decimal(value, scaleA);
                referenceA = BigDecimal.valueOf(value, scaleA);
            } else {
                BigInteger value = randomBigInteger();
                decimalA = Decimal.decimal(value, scaleA);
                referenceA = new BigDecimal(value, scaleA);
            }

            int scaleB = pickRandomValue(scaleA, randomInt(-20, 20));
            if (randomBoolean()) {
                long value = randomLong();
                decimalB = Decimal.decimal(value, scaleB);
                referenceB = BigDecimal.valueOf(value, scaleB);
            } else {
                BigInteger value = randomBigInteger();
                decimalB = Decimal.decimal(value, scaleB);
                referenceB = new BigDecimal(value, scaleB);
            }

            assertThat(decimalA.plus(decimalB).bigDecimalValue(), comparesEqualTo(referenceA.add(referenceB)));
        }
    }

    @Test
    public void shouldSubtractValue() {
        for (int i = 0; i < 25_000; i++) {

            Decimal decimalA;
            Decimal decimalB;
            BigDecimal referenceA;
            BigDecimal referenceB;

            int scaleA = randomInt(-20, 20);
            if (randomBoolean()) {
                long value = randomLong();
                decimalA = Decimal.decimal(value, scaleA);
                referenceA = BigDecimal.valueOf(value, scaleA);
            } else {
                BigInteger value = randomBigInteger();
                decimalA = Decimal.decimal(value, scaleA);
                referenceA = new BigDecimal(value, scaleA);
            }

            int scaleB = pickRandomValue(scaleA, randomInt(-20, 20));
            if (randomBoolean()) {
                long value = randomLong();
                decimalB = Decimal.decimal(value, scaleB);
                referenceB = BigDecimal.valueOf(value, scaleB);
            } else {
                BigInteger value = randomBigInteger();
                decimalB = Decimal.decimal(value, scaleB);
                referenceB = new BigDecimal(value, scaleB);
            }

            assertThat(decimalA.minus(decimalB).bigDecimalValue(), comparesEqualTo(referenceA.subtract(referenceB)));
        }
    }

    @Test
    public void shouldMultiplyValue() {
        for (int i = 0; i < 25_000; i++) {

            Decimal decimalA;
            Decimal decimalB;
            BigDecimal referenceA;
            BigDecimal referenceB;

            int scaleA = randomInt(-20, 20);
            if (randomBoolean()) {
                long value = randomLong();
                decimalA = Decimal.decimal(value, scaleA);
                referenceA = BigDecimal.valueOf(value, scaleA);
            } else {
                BigInteger value = randomBigInteger();
                decimalA = Decimal.decimal(value, scaleA);
                referenceA = new BigDecimal(value, scaleA);
            }

            int scaleB = pickRandomValue(scaleA, randomInt(-20, 20));
            if (randomBoolean()) {
                long value = randomLong();
                decimalB = Decimal.decimal(value, scaleB);
                referenceB = BigDecimal.valueOf(value, scaleB);
            } else {
                BigInteger value = randomBigInteger();
                decimalB = Decimal.decimal(value, scaleB);
                referenceB = new BigDecimal(value, scaleB);
            }

            assertThat(decimalA.times(decimalB).bigDecimalValue(), comparesEqualTo(referenceA.multiply(referenceB)));
            // todo: add comparison with precision set
        }
    }

}