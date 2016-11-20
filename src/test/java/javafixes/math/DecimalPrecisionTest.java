package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.math.Decimal.decimal;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalPrecisionTest {

    @Test
    public void shouldFindNumberOfLongDigits() {
        assertThat(decimal(0, randomInt()).precision(), equalTo(1));
        assertThat(decimal(1, randomInt()).precision(), equalTo(1));
        assertThat(decimal(-1, randomInt()).precision(), equalTo(1));

        assertThat(decimal(Long.MAX_VALUE, randomInt()).precision(), equalTo(19));
        assertThat(decimal(Long.MIN_VALUE, randomInt()).precision(), equalTo(19));

        for (int n = 1; n < 19; n++) {
            long powerOf10 = (long) Math.pow(10, n);
            assertThat(decimal(powerOf10 - 1, randomInt()).precision(), equalTo(n));
            assertThat(decimal(powerOf10 + 1, randomInt()).precision(), equalTo(n + 1));
            assertThat(decimal(-powerOf10 + 1, randomInt()).precision(), equalTo(n));
            assertThat(decimal(-powerOf10 - 1, randomInt()).precision(), equalTo(n + 1));
        }
    }

    @Test
    public void shouldFindNumberOfBigIntegerDigits() {
        BigInteger powerOf10 = BigInteger.ONE;
        // tested for (n) up to 125_000 digits
        for (int n = 1; n < 1_000; n++) {
            powerOf10 = powerOf10.multiply(BigInteger.TEN);
            assertThat(decimal(powerOf10.subtract(BigInteger.ONE), randomInt()).precision(), equalTo(n));
            assertThat(decimal(powerOf10.add(BigInteger.ONE), randomInt()).precision(), equalTo(n + 1));
            assertThat(decimal(powerOf10.negate().add(BigInteger.ONE), randomInt()).precision(), equalTo(n));
            assertThat(decimal(powerOf10.negate().subtract(BigInteger.ONE), randomInt()).precision(), equalTo(n + 1));
        }
    }
}
