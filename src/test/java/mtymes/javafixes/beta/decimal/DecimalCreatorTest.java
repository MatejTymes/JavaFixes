package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertThat;

public class DecimalCreatorTest {

    // todo: add test that the creator will strip trailing zeros

    @Test
    public void shouldBeAbleToCreateDecimalFromLong() {
        for (int i = 0; i < 500_000; i++) {

            long unscaledValue = randomLong();
            int scale = randomInt(-1000, 1000);

            Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);
            BigDecimal referenceValue = BigDecimal.valueOf(unscaledValue, scale);

            assertThat(decimal.bigDecimalValue(), comparesEqualTo(referenceValue));
        }
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
}