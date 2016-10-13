package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalTest {

    @Test
    public void shouldProvideProperSignum() {
        assertThat(Decimal.decimal(0, randomInt()).signum(), equalTo(0));
        assertThat(Decimal.decimal(randomLong(positive()), randomInt()).signum(), equalTo(1));
        assertThat(Decimal.decimal(randomLong(negative()), randomInt()).signum(), equalTo(-1));

        assertThat(Decimal.decimal(BigInteger.ZERO, randomInt()).signum(), equalTo(0));
        assertThat(Decimal.decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomInt()).signum(), equalTo(1));
        assertThat(Decimal.decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomInt()).signum(), equalTo(-1));
    }
}