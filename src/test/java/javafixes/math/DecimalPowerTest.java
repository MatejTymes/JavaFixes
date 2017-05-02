package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.d;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalPowerTest {

    @Test
    public void shouldCalculatePositivePowerOfDecimal() {
        int power = randomInt(2, 100, notZero());


        int scaleLimit = Integer.MAX_VALUE / power;
        for (int scale : newList(
                -scaleLimit,
                randomInt(-scaleLimit + 1, -1),
                0,
                randomInt(1, scaleLimit - 1),
                scaleLimit
        )) {
            long unscaledValueL = randomLong(notDivisibleBy10());
            Decimal value = d(unscaledValueL, scale);

            // When
            Decimal actualResult = value.pow(power);

            // Then
            Decimal expectedResult = d(
                    BigInteger.valueOf(unscaledValueL).pow(power),
                    scale * power
            );
            assertThat(actualResult, equalTo(expectedResult));


            BigInteger unscaledValueB = randomBigInteger(notDivisibleBy10(), notFitIntoLong());
            value = d(unscaledValueB, scale);

            // When
            actualResult = value.pow(power);

            // Then
            expectedResult = d(
                    unscaledValueB.pow(power),
                    scale * power
            );
            assertThat(actualResult, equalTo(expectedResult));
        }
    }

    @Test
    public void shouldFailCalculatingPositivePowerOnScaleOverflowAndUnderflow() {
        int power = randomInt(2, 100, notZero());

        int scaleLimit = Integer.MAX_VALUE / power;
        for (int scale : newList(
                Integer.MIN_VALUE,
                randomInt(Integer.MIN_VALUE + 1, -scaleLimit - 2)
                        - scaleLimit - 1,
                scaleLimit + 1,
                randomInt(scaleLimit + 2, Integer.MAX_VALUE - 1),
                Integer.MAX_VALUE
        )) {
            long unscaledValueL = randomLong(notDivisibleBy10());
            Decimal value = d(unscaledValueL, scale);

            try {
                // When
                value.pow(power);

                // Then
                fail("expected ArithmeticException");
            } catch (ArithmeticException expected) {
                long expectedScale = (long) scale * power;
                if (expectedScale < 0) {
                    assertThat(expected.getMessage(), equalTo("Scale underflow - can't calculate power of " + power + " as it would resolve into non-integer scale " + expectedScale));
                } else {
                    assertThat(expected.getMessage(), equalTo("Scale overflow - can't calculate power of " + power + " as it would resolve into non-integer scale " + expectedScale));
                }
            }

            BigInteger unscaledValueB = randomBigInteger(notDivisibleBy10(), notFitIntoLong());
            value = d(unscaledValueB, scale);

            try {
                // When
                value.pow(power);

                // Then
                fail("expected ArithmeticException");
            } catch (ArithmeticException expected) {
                long expectedScale = (long) scale * power;
                if (expectedScale < 0) {
                    assertThat(expected.getMessage(), equalTo("Scale underflow - can't calculate power of " + power + " as it would resolve into non-integer scale " + expectedScale));
                } else {
                    assertThat(expected.getMessage(), equalTo("Scale overflow - can't calculate power of " + power + " as it would resolve into non-integer scale " + expectedScale));
                }
            }
        }
    }

    @Test
    public void shouldReturnTheSameValueForPowerOfOne() {
        for (Decimal value : newList(
                d(randomLong(notDivisibleBy10()), randomInt()),
                d(randomBigInteger(notDivisibleBy10(), notFitIntoLong()), randomInt())
        )) {
            assertThat(value.pow(1), equalTo(value));
        }
    }

    @Test
    public void shouldReturnOneForPowerOfZero() {
        for (Decimal value : newList(
                d(randomLong(notDivisibleBy10()), randomInt()),
                d(randomBigInteger(notDivisibleBy10(), notFitIntoLong()), randomInt())
        )) {
            assertThat(value.pow(0), equalTo(Decimal.ONE));
        }
    }

    @Test
    public void shouldFailForNegativePower() {
        for (Decimal value : newList(
                d(randomLong(notDivisibleBy10()), randomInt()),
                d(randomBigInteger(notDivisibleBy10(), notFitIntoLong()), randomInt())
        )) {
            for (int power : newList(Integer.MIN_VALUE, randomInt(Integer.MIN_VALUE + 1, -2), -1)) {

                try {
                    // When
                    value.pow(power);

                    // Then
                    fail("expected ArithmeticException");
                } catch (ArithmeticException expected) {
                    assertThat(expected.getMessage(), equalTo("Can't calculate power using negative exponent " + power));
                }
            }
        }
    }
}
