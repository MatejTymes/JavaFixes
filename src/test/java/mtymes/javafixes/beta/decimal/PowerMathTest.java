package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import static mtymes.javafixes.beta.decimal.PowerMath.maxLongPowerOf10;
import static mtymes.javafixes.beta.decimal.PowerMath.powerOf10Long;
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
}