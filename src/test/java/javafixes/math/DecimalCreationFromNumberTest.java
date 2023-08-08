package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static java.lang.Math.pow;
import static javafixes.collection.util.CollectionUtil.newList;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalCreationFromNumberTest {

    @Test
    public void shouldCreateDecimalFromInt() {
        List<Integer> values = newList(
                0,
                1,
                -1,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                randomInt(2, Integer.MAX_VALUE - 1, notDivisibleBy10()),
                randomInt(Integer.MIN_VALUE + 1, -2, notDivisibleBy10())
        );

        for (int value : values) {
            assertThat(Decimal.decimal(value), equalTo(Decimal.decimal((long) value, 0)));
            assertThat(Decimal.d(value), equalTo(Decimal.decimal((long) value, 0)));
        }
    }


    @Test
    public void shouldCreateLongDecimalFromLong() {
        long unscaledValue = randomLong(notDivisibleBy10());
        int scale = randomInt();

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        assertThat(Decimal.decimal(unscaledValue), equalTo(Decimal.decimal(unscaledValue, 0)));
        assertThat(Decimal.d(unscaledValue), equalTo(Decimal.decimal(unscaledValue, 0)));
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
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
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
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue.longValue()));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
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
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.decimal(unscaledValue);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(0));

        // When
        decimal = Decimal.d(unscaledValue);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(0));
    }

    @Test
    public void shouldCreateHugeDecimalFromBigIntegerAndStripTrailingZeros() {
        int trailingZerosCount = randomInt(1, 1_000);
        BigInteger valueWithoutTrailingZeros = randomBigInteger(notFitIntoLong(), notDivisibleBy10(), notZero());
        BigInteger unscaledValue = valueWithoutTrailingZeros.multiply(BigInteger.TEN.pow(trailingZerosCount));
        int scale = randomInt(Integer.MIN_VALUE + trailingZerosCount, Integer.MAX_VALUE);

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal.unscaledValue(), equalTo(valueWithoutTrailingZeros));
        assertThat(decimal.scale(), equalTo(scale - trailingZerosCount));
    }

    @Test
    public void shouldFailToCreateHugeDecimalIfThereIsAScaleOverflowWhileStrippingTrailingZeros() {
        int trailingZerosCount = randomInt(1, 1_000);
        BigInteger valueWithoutTrailingZeros = randomBigInteger(notFitIntoLong(), notDivisibleBy10(), notZero());
        BigInteger unscaledValue = valueWithoutTrailingZeros.multiply(BigInteger.TEN.pow(trailingZerosCount));
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
    public void shouldHaveZeroScaleForZeroValue() {
        Decimal decimal = Decimal.decimal(0, randomInt(notZero()));
        assertThat(decimal.unscaledValue(), equalTo(0L));
        assertThat(decimal.scale(), equalTo(0));

        decimal = Decimal.d(0, randomInt(notZero()));
        assertThat(decimal.unscaledValue(), equalTo(0L));
        assertThat(decimal.scale(), equalTo(0));

        decimal = Decimal.decimal(BigInteger.ZERO, randomInt(notZero()));
        assertThat(decimal.unscaledValue(), equalTo(0L));
        assertThat(decimal.scale(), equalTo(0));

        decimal = Decimal.d(BigInteger.ZERO, randomInt(notZero()));
        assertThat(decimal.unscaledValue(), equalTo(0L));
        assertThat(decimal.scale(), equalTo(0));
    }
}