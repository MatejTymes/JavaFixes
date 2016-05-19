package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static mtymes.javafixes.test.Condition.negative;
import static mtymes.javafixes.test.Condition.positive;
import static mtymes.javafixes.test.Random.randomInt;
import static mtymes.javafixes.test.Random.randomLong;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PowerMathTest {

    @Test
    public void shouldFindMaxPowerOf10ForLong() {
        assertThat(PowerMath.maxLongPowerOf10(), equalTo(18));
    }

    @Test
    public void shouldFindPowerOf10ForLongs() {
        assertThat(PowerMath.powerOf10Long(0), equalTo(1L));

        long expectedValue = 1L;
        for (int i = 1; i <= 18; i++) {
            expectedValue *= 10L;
            assertThat(PowerMath.powerOf10Long(i), equalTo(expectedValue));
        }
    }

    @Test
    public void shouldFindPowerOf10ForBigIntegers() {
        assertThat(PowerMath.powerOf10Big(0), equalTo(BigInteger.ONE));

        BigInteger expectedValue = BigInteger.ONE;
        for (int i = 1; i <= 500; i++) {
            expectedValue = expectedValue.multiply(BigInteger.TEN);
            assertThat(PowerMath.powerOf10Big(i), equalTo(expectedValue));
        }
    }

    @Test
    public void shouldFindIfLongValueCanBeUpscaledByPowerOf10() {
        assertThat(PowerMath.canUpscaleLongByPowerOf10(Long.MAX_VALUE, 0), is(true));
        assertThat(PowerMath.canUpscaleLongByPowerOf10(Long.MIN_VALUE, 0), is(true));
        assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(Long.MIN_VALUE, Long.MAX_VALUE), 0), is(true));

        for (int n = 1; n <= 18; n++) {
            long nPow10 = PowerMath.powerOf10Long(n);

            assertThat(PowerMath.canUpscaleLongByPowerOf10(Long.MAX_VALUE / nPow10, n), is(true));
            assertThat(PowerMath.canUpscaleLongByPowerOf10(Long.MIN_VALUE / nPow10, n), is(true));
            assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(Long.MIN_VALUE / nPow10, Long.MAX_VALUE / nPow10), n), is(true));

            assertThat(PowerMath.canUpscaleLongByPowerOf10(Long.MAX_VALUE / nPow10 + 1, n), is(false));
            assertThat(PowerMath.canUpscaleLongByPowerOf10(Long.MIN_VALUE / nPow10 - 1, n), is(false));
            assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(Long.MIN_VALUE, Long.MIN_VALUE / nPow10 - 1), n), is(false));
            assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(Long.MAX_VALUE / nPow10 + 1, Long.MAX_VALUE), n), is(false));
        }

        for (int n = 0; n <= 100; n++) {
            assertThat(PowerMath.canUpscaleLongByPowerOf10(0L, n), is(true));
        }

        assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(positive()), 19), is(false));
        assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(negative()), 19), is(false));

        assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(positive()), randomInt(20, Integer.MAX_VALUE)), is(false));
        assertThat(PowerMath.canUpscaleLongByPowerOf10(randomLong(negative()), randomInt(20, Integer.MAX_VALUE)), is(false));
    }

    @Test
    public void shouldFindNumberOfLongDigits() {
        assertThat("wrong number of digits for " + 0L, PowerMath.numberOfDigits(0L), equalTo(1));
        assertThat("wrong number of digits for " + 1L, PowerMath.numberOfDigits(1L), equalTo(1));
        assertThat("wrong number of digits for " + -1L, PowerMath.numberOfDigits(-1L), equalTo(1));

        for (int n = 1; n < PowerMath.maxLongPowerOf10(); n++) {
            long powerOf10 = PowerMath.powerOf10Long(n);
            assertThat("wrong number of digits for " + powerOf10, PowerMath.numberOfDigits(powerOf10), equalTo(n + 1));
            assertThat("wrong number of digits for " + -powerOf10, PowerMath.numberOfDigits(-powerOf10), equalTo(n + 1));
            assertThat("wrong number of digits for " + (powerOf10 - 1), PowerMath.numberOfDigits(powerOf10 - 1), equalTo(n));
            assertThat("wrong number of digits for " + (-powerOf10 + 1), PowerMath.numberOfDigits(-powerOf10 + 1), equalTo(n));
            assertThat("wrong number of digits for " + (powerOf10 + 1), PowerMath.numberOfDigits(powerOf10 + 1), equalTo(n + 1));
            assertThat("wrong number of digits for " + (-powerOf10 - 1), PowerMath.numberOfDigits(-powerOf10 - 1), equalTo(n + 1));
        }
    }

    @Test
    public void shouldFindNumberOfBigIntegerDigits() {
        assertThat("wrong number of digits for " + ZERO, PowerMath.numberOfDigits(ZERO), equalTo(1));
        assertThat("wrong number of digits for " + ONE, PowerMath.numberOfDigits(ONE), equalTo(1));
        assertThat("wrong number of digits for " + ONE.negate(), PowerMath.numberOfDigits(ONE.negate()), equalTo(1));

        BigInteger powerOf10 = PowerMath.powerOf10Big(0);
        // tested for (n) up to 125_000 digits
        for (int n = 1; n < 1_000; n++) {
            powerOf10 = powerOf10.multiply(BigInteger.TEN);
            assertThat("wrong number of digits for " + powerOf10, PowerMath.numberOfDigits(powerOf10), equalTo(n + 1));
            BigInteger negativePowerOf10 = powerOf10.negate();
            assertThat("wrong number of digits for " + negativePowerOf10, PowerMath.numberOfDigits(negativePowerOf10), equalTo(n + 1));
            assertThat("wrong number of digits for " + powerOf10.subtract(ONE), PowerMath.numberOfDigits(powerOf10.subtract(ONE)), equalTo(n));
            assertThat("wrong number of digits for " + negativePowerOf10.add(ONE), PowerMath.numberOfDigits(negativePowerOf10.add(ONE)), equalTo(n));
            assertThat("wrong number of digits for " + powerOf10.add(ONE), PowerMath.numberOfDigits(powerOf10.add(ONE)), equalTo(n + 1));
            assertThat("wrong number of digits for " + negativePowerOf10.subtract(ONE), PowerMath.numberOfDigits(negativePowerOf10.subtract(ONE)), equalTo(n + 1));
        }
    }
}