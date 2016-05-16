package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static mtymes.javafixes.beta.decimal.PowerMath.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PowerMathTest {

    @Test
    public void shouldFindMaxPowerOf10ForLong() {
        assertThat(PowerMath.maxLongPowerOf10(), equalTo(18));
    }

    @Test
    public void shouldFindPowerOf10ForLongs() {
        assertThat(powerOf10Long(0), equalTo(1L));

        long expectedValue = 1L;
        for (int i = 1; i <= 18; i++) {
            expectedValue *= 10L;
            assertThat(powerOf10Long(i), equalTo(expectedValue));
        }
    }

    @Test
    public void shouldFindPowerOf10ForBigIntegers() {
        assertThat(powerOf10Big(0), equalTo(BigInteger.ONE));

        BigInteger expectedValue = BigInteger.ONE;
        for (int i = 1; i <= 500; i++) {
            expectedValue = expectedValue.multiply(BigInteger.TEN);
            assertThat(powerOf10Big(i), equalTo(expectedValue));
        }
    }


    @Test
    public void shouldFindNumberOfLongDigits() {
        assertThat(PowerMath.numberOfDigits(0L), equalTo(1));
        assertThat(PowerMath.numberOfDigits(1L), equalTo(1));
        assertThat(PowerMath.numberOfDigits(-1L), equalTo(1));

        for (int n = 1; n < maxLongPowerOf10(); n++) {
            long powerOf10 = powerOf10Long(n);
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
        assertThat(PowerMath.numberOfDigits(BigInteger.ZERO), equalTo(1));
        assertThat(PowerMath.numberOfDigits(ONE), equalTo(1));
        assertThat(PowerMath.numberOfDigits(ONE.negate()), equalTo(1));

        for (int n = 1; n < 1_000; n++) {
            BigInteger powerOf10 = powerOf10Big(n);
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