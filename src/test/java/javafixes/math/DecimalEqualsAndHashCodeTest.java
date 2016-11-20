package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static java.lang.Math.pow;
import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.notDivisibleBy10;
import static javafixes.test.Condition.notFitIntoLong;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class DecimalEqualsAndHashCodeTest {

    @Test
    public void shouldFindDecimalsAreEqual() {
        int scaleA = randomInt(-1000, 1000);
        int scaleDiff = randomInt(1, 10);
        int scaleB = scaleA + scaleDiff;

        long unscaledValueLA = randomLong(-999999L, 9999999L);
        long unscaledValueLB = unscaledValueLA * (long) pow(10, scaleDiff);

        assertThat(decimal(unscaledValueLA, scaleA), equalTo(decimal(unscaledValueLA, scaleA)));
        assertThat(decimal(unscaledValueLA, scaleA), equalTo(decimal(unscaledValueLB, scaleB)));

        BigInteger unscaledValueBA = randomBigInteger(notFitIntoLong(), notFitIntoLong());
        BigInteger unscaledValueBB = unscaledValueBA.multiply(BigInteger.TEN.pow(scaleDiff));

        assertThat(decimal(unscaledValueBA, scaleA), equalTo(decimal(unscaledValueBA, scaleA)));
        assertThat(decimal(unscaledValueBA, scaleA), equalTo(decimal(unscaledValueBB, scaleB)));
    }

    @Test
    public void shouldFindDecimalsAreNotEqual() {
        long unscaledValueL = randomLong();
        BigInteger unscaledValueB = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        int scale = randomInt();

        assertThat(decimal(unscaledValueL, scale), not(equalTo(decimal(unscaledValueL + 1, scale))));
        assertThat(decimal(unscaledValueL, scale), not(equalTo(decimal(unscaledValueL - 1, scale))));
        assertThat(decimal(unscaledValueL, scale), not(equalTo(decimal(unscaledValueL, scale + 1))));
        assertThat(decimal(unscaledValueL, scale), not(equalTo(decimal(unscaledValueL, scale - 1))));

        assertThat(decimal(unscaledValueB, scale), not(equalTo(decimal(unscaledValueB.add(BigInteger.ONE), scale))));
        assertThat(decimal(unscaledValueB, scale), not(equalTo(decimal(unscaledValueB.subtract(BigInteger.ONE), scale))));
        assertThat(decimal(unscaledValueB, scale), not(equalTo(decimal(unscaledValueB, scale + 1))));
        assertThat(decimal(unscaledValueB, scale), not(equalTo(decimal(unscaledValueB, scale - 1))));
    }

    @Test
    public void shouldProvideEqualHashCode() {
        int scaleA = randomInt(-1000, 1000);
        int scaleDiff = randomInt(1, 10);
        int scaleB = scaleA + scaleDiff;

        long unscaledValueLA = randomLong(-999999L, 9999999L);
        long unscaledValueLB = unscaledValueLA * (long) pow(10, scaleDiff);

        assertThat(decimal(unscaledValueLA, scaleA).hashCode(), equalTo(decimal(unscaledValueLA, scaleA).hashCode()));
        assertThat(decimal(unscaledValueLA, scaleA).hashCode(), equalTo(decimal(unscaledValueLB, scaleB).hashCode()));

        BigInteger unscaledValueBA = randomBigInteger(notFitIntoLong(), notFitIntoLong());
        BigInteger unscaledValueBB = unscaledValueBA.multiply(BigInteger.TEN.pow(scaleDiff));

        assertThat(decimal(unscaledValueBA, scaleA).hashCode(), equalTo(decimal(unscaledValueBA, scaleA).hashCode()));
        assertThat(decimal(unscaledValueBA, scaleA).hashCode(), equalTo(decimal(unscaledValueBB, scaleB).hashCode()));
    }
}
