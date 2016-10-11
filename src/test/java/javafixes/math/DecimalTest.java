package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static java.lang.Math.pow;
import static javafixes.math.util.BigIntegerUtil.TEN_AS_BIG_INTEGER;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalTest {

    @Test
    public void shouldCreateLongDecimalFromLong() {
        long unscaledValue = randomLong(notDivisibleBy10());
        int scale = randomInt();

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateLongDecimalFromLongAndStripTrailingZeros() {
        int trailingZerosCount = randomInt(1, 18);
        long multiplier = (long) pow(10, trailingZerosCount);
        long valueWithoutTrailingZeros = randomLong(Long.MIN_VALUE / multiplier, Long.MAX_VALUE / multiplier, notDivisibleBy10(), notZero());
        long unscaledValue = valueWithoutTrailingZeros * multiplier;
        int scale = randomInt(Integer.MIN_VALUE + trailingZerosCount, Integer.MAX_VALUE);

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldFailToCreateLongDecimalIfThereIsAScaleOverflowWhileStrippingTrailingZeros() {
        int trailingZerosCount = randomInt(1, 18);
        long multiplier = (long) pow(10, trailingZerosCount);
        long valueWithoutTrailingZeros = randomLong(Long.MIN_VALUE / multiplier, Long.MAX_VALUE / multiplier, notDivisibleBy10(), notZero());
        long unscaledValue = valueWithoutTrailingZeros * multiplier;
        int scale = randomInt(Integer.MIN_VALUE, Integer.MIN_VALUE + trailingZerosCount - 1);

        try {
            // When
            Decimal.decimal(unscaledValue, scale);

            // Then
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }

        try {
            // When
            Decimal.d(unscaledValue, scale);

            // Then
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void shouldCreateLongDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(fitsIntoLong(), notDivisibleBy10());
        int scale = randomInt();

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue.longValue()));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue.longValue()));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateHugeDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        int scale = randomInt();

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateHugeDecimalFromBigIntegerAndStripTrailingZeros() {
        int trailingZerosCount = randomInt(1, 1_000);
        BigInteger valueWithoutTrailingZeros = randomBigInteger(notFitIntoLong(), notDivisibleBy10(), notZero());
        BigInteger unscaledValue = valueWithoutTrailingZeros.multiply(TEN_AS_BIG_INTEGER.pow(trailingZerosCount));
        int scale = randomInt(Integer.MIN_VALUE + trailingZerosCount, Integer.MAX_VALUE);

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldFailToCreateHugeDecimalIfThereIsAScaleOverflowWhileStrippingTrailingZeros() {
        int trailingZerosCount = randomInt(1, 1_000);
        BigInteger valueWithoutTrailingZeros = randomBigInteger(notFitIntoLong(), notDivisibleBy10(), notZero());
        BigInteger unscaledValue = valueWithoutTrailingZeros.multiply(TEN_AS_BIG_INTEGER.pow(trailingZerosCount));
        int scale = randomInt(Integer.MIN_VALUE, Integer.MIN_VALUE + trailingZerosCount - 1);

        try {
            // When
            Decimal.decimal(unscaledValue, scale);

            // Then
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }

        try {
            // When
            Decimal.d(unscaledValue, scale);

            // Then
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }
}