package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertThat;

public class DecimalReferenceTest {

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