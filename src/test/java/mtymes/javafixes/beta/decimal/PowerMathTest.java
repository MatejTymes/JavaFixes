package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static mtymes.javafixes.beta.decimal.PowerMath.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PowerMathTest {

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