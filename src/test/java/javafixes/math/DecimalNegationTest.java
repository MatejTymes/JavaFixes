package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalNegationTest {

    @Test
    public void shouldNegateLongDecimal() {
        int scale = randomInt();

        assertThat(decimal(0, scale).negate(), equalTo(decimal(0, 0)));

        long positiveValue = randomLong(1, Long.MAX_VALUE - 1, notDivisibleBy10());
        assertThat(decimal(positiveValue, scale).negate(), equalTo(decimal(-positiveValue, scale)));
        assertThat(decimal(Long.MAX_VALUE, scale).negate(), equalTo(decimal(BigInteger.valueOf(Long.MAX_VALUE).negate(), scale)));

        long negativeValue = randomLong(Long.MIN_VALUE + 1, -1, notDivisibleBy10());
        assertThat(decimal(negativeValue, scale).negate(), equalTo(decimal(-negativeValue, scale)));
        assertThat(decimal(Long.MIN_VALUE, scale).negate(), equalTo(decimal(BigInteger.valueOf(Long.MIN_VALUE).negate(), scale)));
    }

    @Test
    public void shouldNegateHugeDecimal() {
        int scale = randomInt();

        BigInteger positiveValue = randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10());
        assertThat(decimal(positiveValue, scale).negate(), equalTo(decimal(positiveValue.negate(), scale)));

        BigInteger negativeValue = randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10());
        assertThat(decimal(negativeValue, scale).negate(), equalTo(decimal(negativeValue.negate(), scale)));
    }
}
