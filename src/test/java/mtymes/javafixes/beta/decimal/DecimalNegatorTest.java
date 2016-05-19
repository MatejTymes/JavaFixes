package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigInteger;

import static mtymes.javafixes.beta.decimal.Decimal.decimal;
import static mtymes.javafixes.test.Condition.*;
import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalNegatorTest {

    public static final BigInteger BIG_INTEGER_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    @Test
    public void shouldNegateDecimal() {

        int scale = randomInt();

        assertThat(DecimalNegator.negate(decimal(0, scale)), equalTo(decimal(0L, 0)));
        assertThat(DecimalNegator.negate(decimal(BigInteger.ZERO, scale)), equalTo(decimal(0L, 0)));

        assertThat(DecimalNegator.negate(decimal(Long.MAX_VALUE, scale)), equalTo(decimal(-Long.MAX_VALUE, scale)));
        assertThat(DecimalNegator.negate(decimal(Long.MIN_VALUE, scale)), equalTo(decimal(BIG_INTEGER_LONG_MIN.negate(), scale)));
        assertThat(DecimalNegator.negate(decimal(BIG_INTEGER_LONG_MIN.negate(), scale)), equalTo(decimal(Long.MIN_VALUE, scale)));

        long unscaledValueL = randomLong(positive(), notDivisibleBy10());
        assertThat(DecimalNegator.negate(decimal(unscaledValueL, scale)), equalTo(decimal(-unscaledValueL, scale)));

        unscaledValueL = randomLong(negative(), notDivisibleBy10(), otherThan(Long.MIN_VALUE));
        assertThat(DecimalNegator.negate(decimal(unscaledValueL, scale)), equalTo(decimal(-unscaledValueL, scale)));

        BigInteger unscaledValueB = randomBigInteger(positive(), notDivisibleBy10(), notFitIntoLong());
        assertThat(DecimalNegator.negate(decimal(unscaledValueB, scale)), equalTo(decimal(unscaledValueB.negate(), scale)));

        unscaledValueB = randomBigInteger(negative(), notDivisibleBy10(), notFitIntoLong(), otherThan(BIG_INTEGER_LONG_MIN));
        assertThat(DecimalNegator.negate(decimal(unscaledValueB, scale)), equalTo(decimal(unscaledValueB.negate(), scale)));
    }
}