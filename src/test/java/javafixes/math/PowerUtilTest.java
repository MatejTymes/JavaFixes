package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static javafixes.test.Condition.negative;
import static javafixes.test.Condition.positive;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PowerUtilTest {

    @Test
    public void shouldUpscaleLongByPowerOf10() {
        long value;

        assertThat(PowerUtil.upscaleByPowerOf10(0L, 0L), equalTo(0L));

        value = randomLong(positive());
        assertThat(PowerUtil.upscaleByPowerOf10(value, 0L), equalTo(value));

        value = randomLong(negative());
        assertThat(PowerUtil.upscaleByPowerOf10(value, 0L), equalTo(value));

        for (long n = 1L; n < 18L; n++) {
            assertThat(PowerUtil.upscaleByPowerOf10(0L, n), equalTo(0L));

            long powerOf10 = (long) Math.pow(10, n);

            value = Long.MAX_VALUE / powerOf10;
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value * powerOf10));

            value = 1;
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value * powerOf10));

            value = randomLong(2, Long.MAX_VALUE / powerOf10 - 1);
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value * powerOf10));

            value = Long.MIN_VALUE / powerOf10;
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value * powerOf10));

            value = -1;
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value * powerOf10));

            value = randomLong(Long.MIN_VALUE / powerOf10 + 1, -2);
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value * powerOf10));
        }

        assertThat(PowerUtil.upscaleByPowerOf10(0L, 19), equalTo(0L));
        assertThat(PowerUtil.upscaleByPowerOf10(0L, randomLong(20, Long.MAX_VALUE)), equalTo(0L));
    }

    @Test
    public void shouldNotUpscaleLongByPowerOf10IfResultWouldOverflow() {
        long value;

        for (long n = 1L; n < 18L; n++) {
            long powerOf10 = (long) Math.pow(10, n);

            value = 1 + Long.MAX_VALUE / powerOf10;
            try {
                PowerUtil.upscaleByPowerOf10(value, n);
                fail("expected ArithmeticException");
            } catch (ArithmeticException expected) {
                // expected
            }

            value = randomLong(2 + Long.MAX_VALUE / powerOf10, Long.MAX_VALUE);
            try {
                PowerUtil.upscaleByPowerOf10(value, n);
                fail("expected ArithmeticException");
            } catch (ArithmeticException expected) {
                // expected
            }

            value = -1 + Long.MIN_VALUE / powerOf10;
            try {
                PowerUtil.upscaleByPowerOf10(value, n);
                fail("expected ArithmeticException");
            } catch (ArithmeticException expected) {
                // expected
            }

            value = randomLong(Long.MIN_VALUE, -1 + (Long.MIN_VALUE / powerOf10));
            try {
                PowerUtil.upscaleByPowerOf10(value, n);
                fail("expected ArithmeticException");
            } catch (ArithmeticException expected) {
                // expected
            }
        }
    }

    @Test
    public void shouldDownscaleLong() {
        long value;

        assertThat(PowerUtil.downscaleByPowerOf10(0L, 0L), equalTo(0L));

        value = randomLong(positive());
        assertThat(PowerUtil.downscaleByPowerOf10(value, 0L), equalTo(value));

        value = randomLong(negative());
        assertThat(PowerUtil.downscaleByPowerOf10(value, 0L), equalTo(value));

        for (long n = 1L; n < 18L; n++) {
            assertThat(PowerUtil.downscaleByPowerOf10(0L, n), equalTo(0L));

            long powerOf10 = (long) Math.pow(10, n);

            value = powerOf10;
            assertThat(PowerUtil.downscaleByPowerOf10(value, n), equalTo(1L));

            value = powerOf10 - 1;
            assertThat(PowerUtil.downscaleByPowerOf10(value, n), equalTo(0L));

            value = randomLong(powerOf10 + 1, Long.MAX_VALUE);
            assertThat(PowerUtil.downscaleByPowerOf10(value, n), equalTo(value / powerOf10));

            value = -powerOf10;
            assertThat(PowerUtil.downscaleByPowerOf10(value, n), equalTo(-1L));

            value = -powerOf10 + 1;
            assertThat(PowerUtil.downscaleByPowerOf10(value, n), equalTo(0L));

            value = randomLong(Long.MIN_VALUE, -powerOf10 - 1);
            assertThat(PowerUtil.downscaleByPowerOf10(value, n), equalTo(value / powerOf10));
        }

        assertThat(PowerUtil.downscaleByPowerOf10(randomLong(positive()), 19), equalTo(0L));
        assertThat(PowerUtil.downscaleByPowerOf10(Long.MAX_VALUE, 19), equalTo(0L));
        assertThat(PowerUtil.downscaleByPowerOf10(randomLong(negative()), 19), equalTo(0L));
        assertThat(PowerUtil.downscaleByPowerOf10(Long.MIN_VALUE, 19), equalTo(0L));

        long n = randomLong(20, Long.MAX_VALUE);

        assertThat(PowerUtil.downscaleByPowerOf10(randomLong(positive()), n), equalTo(0L));
        assertThat(PowerUtil.downscaleByPowerOf10(Long.MAX_VALUE, n), equalTo(0L));
        assertThat(PowerUtil.downscaleByPowerOf10(randomLong(negative()), n), equalTo(0L));
        assertThat(PowerUtil.downscaleByPowerOf10(Long.MIN_VALUE, n), equalTo(0L));
    }

    @Test
    public void shouldUpscaleBigInteger() {
        BigInteger value;

        assertThat(PowerUtil.upscaleByPowerOf10(BigInteger.ZERO, 0L), equalTo(BigInteger.ZERO));
        value = randomBigInteger(positive());
        assertThat(PowerUtil.upscaleByPowerOf10(value, 0L), equalTo(value));
        value = randomBigInteger(negative());
        assertThat(PowerUtil.upscaleByPowerOf10(value, 0L), equalTo(value));

        for (int n = 1; n < 100; n++) {
            assertThat(PowerUtil.upscaleByPowerOf10(BigInteger.ZERO, n), equalTo(BigInteger.ZERO));

            BigInteger powerOf10 = BigInteger.TEN;
            for (int i = 1; i < n; i++) {
                powerOf10 = powerOf10.multiply(BigInteger.TEN);
            }

            value = randomBigInteger(positive());
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value.multiply(powerOf10)));

            value = randomBigInteger(negative());
            assertThat(PowerUtil.upscaleByPowerOf10(value, n), equalTo(value.multiply(powerOf10)));
        }
    }

    @Test
    public void shouldFindNumberOfLongDigits() {
        assertThat("wrong number of digits for " + 0L, PowerUtil.numberOfDigits(0L), equalTo(1));
        assertThat("wrong number of digits for " + 1L, PowerUtil.numberOfDigits(1L), equalTo(1));
        assertThat("wrong number of digits for " + -1L, PowerUtil.numberOfDigits(-1L), equalTo(1));

        assertThat("wrong number of digits for " + Long.MAX_VALUE, PowerUtil.numberOfDigits(Long.MAX_VALUE), equalTo(19));
        assertThat("wrong number of digits for " + Long.MIN_VALUE, PowerUtil.numberOfDigits(Long.MIN_VALUE), equalTo(19));

        for (int n = 1; n < 19; n++) {
            long powerOf10 = (long) Math.pow(10, n);
            assertThat("wrong number of digits for " + powerOf10, PowerUtil.numberOfDigits(powerOf10), equalTo(n + 1));
            assertThat("wrong number of digits for " + -powerOf10, PowerUtil.numberOfDigits(-powerOf10), equalTo(n + 1));
            assertThat("wrong number of digits for " + (powerOf10 - 1), PowerUtil.numberOfDigits(powerOf10 - 1), equalTo(n));
            assertThat("wrong number of digits for " + (-powerOf10 + 1), PowerUtil.numberOfDigits(-powerOf10 + 1), equalTo(n));
            assertThat("wrong number of digits for " + (powerOf10 + 1), PowerUtil.numberOfDigits(powerOf10 + 1), equalTo(n + 1));
            assertThat("wrong number of digits for " + (-powerOf10 - 1), PowerUtil.numberOfDigits(-powerOf10 - 1), equalTo(n + 1));
        }
    }

    @Test
    public void shouldFindNumberOfBigIntegerDigits() {
        assertThat("wrong number of digits for " + ZERO, PowerUtil.numberOfDigits(ZERO), equalTo(1));
        assertThat("wrong number of digits for " + ONE, PowerUtil.numberOfDigits(ONE), equalTo(1));
        assertThat("wrong number of digits for " + ONE.negate(), PowerUtil.numberOfDigits(ONE.negate()), equalTo(1));

        BigInteger powerOf10 = BigInteger.ONE;
        // tested for (n) up to 125_000 digits
        for (int n = 1; n < 1_000; n++) {
            powerOf10 = powerOf10.multiply(BigInteger.TEN);
            assertThat("wrong number of digits for " + powerOf10, PowerUtil.numberOfDigits(powerOf10), equalTo(n + 1));
            BigInteger negativePowerOf10 = powerOf10.negate();
            assertThat("wrong number of digits for " + negativePowerOf10, PowerUtil.numberOfDigits(negativePowerOf10), equalTo(n + 1));
            assertThat("wrong number of digits for " + powerOf10.subtract(ONE), PowerUtil.numberOfDigits(powerOf10.subtract(ONE)), equalTo(n));
            assertThat("wrong number of digits for " + negativePowerOf10.add(ONE), PowerUtil.numberOfDigits(negativePowerOf10.add(ONE)), equalTo(n));
            assertThat("wrong number of digits for " + powerOf10.add(ONE), PowerUtil.numberOfDigits(powerOf10.add(ONE)), equalTo(n + 1));
            assertThat("wrong number of digits for " + negativePowerOf10.subtract(ONE), PowerUtil.numberOfDigits(negativePowerOf10.subtract(ONE)), equalTo(n + 1));
        }
    }

    @Test
    public void shouldProvidePositiveMaxCachedBigPowerOf10() {
        assertThat(PowerUtil.maxCachedBigPowerOf10(), greaterThan(0));
    }

    @Test
    public void shouldFindBigIntegerPowerOf10() {
        BigInteger expectedPowerOf10 = BigInteger.ONE;

        assertThat(PowerUtil.powerOf10Big(0), equalTo(expectedPowerOf10));

        for (int i = 1; i < 200; i++) {
            expectedPowerOf10 = expectedPowerOf10.multiply(BigInteger.TEN);
            assertThat(PowerUtil.powerOf10Big(i), equalTo(expectedPowerOf10));
        }

        try {
            PowerUtil.powerOf10Big(-1);
            fail("should fail with IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            PowerUtil.powerOf10Big(Integer.MIN_VALUE);
            fail("should fail with IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            PowerUtil.powerOf10Big(randomInt(Integer.MIN_VALUE + 1, -2));
            fail("should fail with IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

}