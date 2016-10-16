package javafixes.beta.decimal;

import javafixes.beta.decimal.Decimal.HugeDecimal;
import javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import java.math.BigInteger;

import static java.lang.Math.pow;
import static javafixes.beta.decimal.Decimal.decimal;
import static javafixes.test.Condition.otherThan;
import static javafixes.test.Random.randomInt;
import static javafixes.test.Random.randomLong;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DecimalEqualizerTest {


    @Test
    public void shouldFindOutSameValuesAreEqual() {
        long unscaledValue = randomLong(-999999L, 9999999L);
        int scaleA = randomInt(-1000, 1000);
        int scaleDiff = randomInt(1, 10);
        int scaleB = scaleA + scaleDiff;

        LongDecimal longDecimalA = new LongDecimal(unscaledValue, scaleA);
        LongDecimal longDecimalB = new LongDecimal(unscaledValue * (long) pow(10, scaleDiff), scaleB);
        HugeDecimal hugeDecimalA = new HugeDecimal(BigInteger.valueOf(unscaledValue), scaleA);
        HugeDecimal hugeDecimalB = new HugeDecimal(BigInteger.valueOf(unscaledValue * (long) pow(10, scaleDiff)), scaleB);

        assertThat(DecimalEqualizer.areEqual(longDecimalA, longDecimalB), is(true));
        assertThat(DecimalEqualizer.areEqual(longDecimalB, longDecimalA), is(true));

        assertThat(DecimalEqualizer.areEqual(longDecimalA, hugeDecimalA), is(true));
        assertThat(DecimalEqualizer.areEqual(hugeDecimalA, longDecimalA), is(true));

        assertThat(DecimalEqualizer.areEqual(longDecimalA, hugeDecimalB), is(true));
        assertThat(DecimalEqualizer.areEqual(hugeDecimalB, longDecimalA), is(true));

        assertThat(DecimalEqualizer.areEqual(longDecimalB, hugeDecimalA), is(true));
        assertThat(DecimalEqualizer.areEqual(hugeDecimalA, longDecimalB), is(true));

        assertThat(DecimalEqualizer.areEqual(longDecimalB, hugeDecimalB), is(true));
        assertThat(DecimalEqualizer.areEqual(hugeDecimalB, longDecimalB), is(true));

        assertThat(DecimalEqualizer.areEqual(hugeDecimalA, hugeDecimalB), is(true));
        assertThat(DecimalEqualizer.areEqual(hugeDecimalB, hugeDecimalA), is(true));

        long unscaledValueA = randomLong();
        long unscaledValueB = randomLong(otherThan(unscaledValueA));
        assertThat(DecimalEqualizer.areEqual(decimal(unscaledValueA, scaleA), decimal(unscaledValueB, scaleA)), is(false));
        assertThat(DecimalEqualizer.areEqual(new HugeDecimal(BigInteger.valueOf(unscaledValueA), scaleA), new HugeDecimal(BigInteger.valueOf(unscaledValueB), scaleA)), is(false));
        assertThat(DecimalEqualizer.areEqual(decimal(unscaledValueA, scaleA), new HugeDecimal(BigInteger.valueOf(unscaledValueB), scaleA)), is(false));
    }

    @Test
    public void shouldProduceTheSameHashCode() {
        long unscaledValue = randomLong(-999999L, 9999999L);
        int scaleA = randomInt(-1000, 1000);
        int scaleDiff = randomInt(1, 10);
        int scaleB = scaleA + scaleDiff;


        LongDecimal longDecimalA = new LongDecimal(unscaledValue, scaleA);
        LongDecimal longDecimalB = new LongDecimal(unscaledValue * (long) pow(10, scaleDiff), scaleB);
        HugeDecimal hugeDecimalA = new HugeDecimal(BigInteger.valueOf(unscaledValue), scaleA);
        HugeDecimal hugeDecimalB = new HugeDecimal(BigInteger.valueOf(unscaledValue * (long) pow(10, scaleDiff)), scaleB);

        assertThat(DecimalEqualizer.hashCode(longDecimalA), equalTo(DecimalEqualizer.hashCode(longDecimalB)));
        assertThat(DecimalEqualizer.hashCode(longDecimalA), equalTo(DecimalEqualizer.hashCode(hugeDecimalA)));
        assertThat(DecimalEqualizer.hashCode(hugeDecimalA), equalTo(DecimalEqualizer.hashCode(hugeDecimalB)));

        LongDecimal longDecimalAPlus1 = new LongDecimal(unscaledValue + 1, scaleA);
        HugeDecimal hugeDecimalAPlus1 = new HugeDecimal(BigInteger.valueOf(unscaledValue + 1), scaleA);

        assertThat(DecimalEqualizer.hashCode(longDecimalA), not(equalTo(DecimalEqualizer.hashCode(longDecimalAPlus1))));
        assertThat(DecimalEqualizer.hashCode(longDecimalA), not(equalTo(DecimalEqualizer.hashCode(hugeDecimalAPlus1))));
    }
}