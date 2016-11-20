package javafixes.math;

import org.junit.Test;

import static javafixes.math.Decimal.d;
import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalAbsoluteTest {

    @Test
    public void shouldDoAbsOnLongDecimal() {
        assertThat(decimal(0, randomInt()).abs(), equalTo(decimal(0, 0)));

        Decimal positiveDecimal = d(randomLong(positive()), randomInt());
        assertThat(positiveDecimal.abs(), equalTo(positiveDecimal));

        Decimal negativeDecimal = d(randomLong(negative()), randomInt());
        assertThat(negativeDecimal.abs(), equalTo(negativeDecimal.negate()));
    }

    @Test
    public void shouldDoAbsOnHugeDecimal() {
        Decimal positiveDecimal = d(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomInt());
        assertThat(positiveDecimal.abs(), equalTo(positiveDecimal));

        Decimal negativeDecimal = d(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomInt());
        assertThat(negativeDecimal.abs(), equalTo(negativeDecimal.negate()));
    }
}
