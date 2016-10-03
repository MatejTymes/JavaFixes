package javafixes.beta.decimal;

import javafixes.beta.decimal.Decimal.HugeDecimal;
import javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import java.math.BigInteger;

import static javafixes.beta.decimal.PowerMath.upscaleByPowerOf10;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class DecimalCreatorTest {

    @Test
    public void shouldCreateDecimalFromLong() {
        long unscaledValue = randomLong(notDivisibleBy10());
        int scale = randomInt();

        Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);

        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateHugeDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        int scale = randomInt();

        Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);

        assertThat(decimal, instanceOf(HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateLongDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(fitsIntoLong(), notDivisibleBy10());
        int scale = randomInt();

        Decimal decimal = DecimalCreator.createDecimal(unscaledValue, scale);

        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue.longValueExact()));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldStripTrailingZerosFromLongOnCreation() {

        long baseValue = randomInt(notDivisibleBy10());
        int scale = randomInt(-1_000, 1_000);

        int trailingZerosCount = randomInt(1, 9);
        long valueWithTrailingZeros = upscaleByPowerOf10(baseValue, trailingZerosCount);

        Decimal decimal = DecimalCreator.createDecimal(valueWithTrailingZeros, scale);

        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(baseValue));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldStripTrailingZerosFromBigIntegerOnCreation() {

        BigInteger baseValue = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        int scale = randomInt(-1_000, 1_000);

        int trailingZerosCount = randomInt(1, 9);
        BigInteger valueWithTrailingZeros = upscaleByPowerOf10(baseValue, trailingZerosCount);

        Decimal decimal = DecimalCreator.createDecimal(valueWithTrailingZeros, scale);

        assertThat(decimal, instanceOf(HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(baseValue));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldStringTrailingZerosAndSwitchFromBigIntegerToLongOnCreation() {

        long baseValue = randomLong(notDivisibleBy10());
        int scale = randomInt(-1_000, 1_000);

        int trailingZerosCount = randomInt(19, 40);
        BigInteger valueWithTrailingZeros = upscaleByPowerOf10(BigInteger.valueOf(baseValue), trailingZerosCount);

        Decimal decimal = DecimalCreator.createDecimal(valueWithTrailingZeros, scale);

        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(baseValue));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));
    }
}