package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.test.Conditions;
import org.junit.Test;

import java.math.BigInteger;

import static mtymes.javafixes.beta.decimal.PowerMath.upscaleByPowerOf10;
import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalCreatorTest {

    @Test
    public void shouldCreateDecimalFromLong() {
        long unscaledValue = randomLong(Conditions.notDivisibleBy10());
        int scale = randomInt();

        assertThat(DecimalCreator.createDecimal(unscaledValue, scale).unscaledValue(), equalTo(unscaledValue));
        assertThat(DecimalCreator.createDecimal(unscaledValue, scale).scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(Conditions.that(Conditions.doesNotFitIntoLong(), Conditions.notDivisibleBy10()));
        int scale = randomInt();

        assertThat(DecimalCreator.createDecimal(unscaledValue, scale).unscaledValue(), equalTo(unscaledValue));
        assertThat(DecimalCreator.createDecimal(unscaledValue, scale).scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateLongDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(Conditions.that(Conditions.doesFitIntoLong(), Conditions.notDivisibleBy10()));
        int scale = randomInt();

        assertThat(DecimalCreator.createDecimal(unscaledValue, scale).unscaledValue(), equalTo(unscaledValue.longValueExact()));
        assertThat(DecimalCreator.createDecimal(unscaledValue, scale).scale(), equalTo(scale));
    }

    @Test
    public void shouldStripTrailingZerosFromLongOnCreation() {

        long baseValue = randomInt(Conditions.notDivisibleBy10());
        int scale = randomInt(-1_000, 1_000);

        int trailingZerosCount = randomInt(1, 9);
        long valueWithTrailingZeros = upscaleByPowerOf10(baseValue, trailingZerosCount);

        assertThat(DecimalCreator.createDecimal(valueWithTrailingZeros, scale).unscaledValue(), equalTo(baseValue));
        assertThat(DecimalCreator.createDecimal(valueWithTrailingZeros, scale).scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldStripTrailingZerosFromBigIntegerOnCreation() {

        BigInteger baseValue = randomBigInteger(Conditions.that(Conditions.doesNotFitIntoLong(), Conditions.notDivisibleBy10()));
        int scale = randomInt(-1_000, 1_000);

        int trailingZerosCount = randomInt(1, 9);
        BigInteger valueWithTrailingZeros = upscaleByPowerOf10(baseValue, trailingZerosCount);

        assertThat(DecimalCreator.createDecimal(valueWithTrailingZeros, scale).unscaledValue(), equalTo(baseValue));
        assertThat(DecimalCreator.createDecimal(valueWithTrailingZeros, scale).scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldStringTrailingZerosAndSwitchFromBigIntegerToLongOnCreation() {

        long baseValue = randomLong(Conditions.notDivisibleBy10());
        int scale = randomInt(-1_000, 1_000);

        int trailingZerosCount = randomInt(19, 40);
        BigInteger valueWithTrailingZeros = upscaleByPowerOf10(BigInteger.valueOf(baseValue), trailingZerosCount);

        assertThat(DecimalCreator.createDecimal(valueWithTrailingZeros, scale).unscaledValue(), equalTo(baseValue));
        assertThat(DecimalCreator.createDecimal(valueWithTrailingZeros, scale).scale(), equalTo(scale - trailingZerosCount));
    }
}