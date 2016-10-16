package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;
import java.util.function.Function;

import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DecimalComparisonTest {

    @Test
    public void shouldCompareDecimalsWithDifferentSignum() {

        assertThat(decimal(randomLong(negative()), randomScaleInt()), lessThan(decimal(0L, randomScaleInt())));
        assertThat(decimal(randomLong(negative()), randomScaleInt()), lessThan(decimal(randomLong(positive()), randomScaleInt())));
        assertThat(decimal(randomLong(negative()), randomScaleInt()), lessThan(decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt())));

        assertThat(decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt()), lessThan(decimal(0L, randomScaleInt())));
        assertThat(decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt()), lessThan(decimal(randomLong(positive()), randomScaleInt())));
        assertThat(decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt()), lessThan(decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt())));

        assertThat(decimal(0L, randomScaleInt()), greaterThan(decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt())));
        assertThat(decimal(0L, randomScaleInt()), greaterThan(decimal(randomLong(negative()), randomScaleInt())));
        assertThat(decimal(0L, randomScaleInt()), comparesEqualTo(decimal(0L, randomScaleInt())));
        assertThat(decimal(0L, randomScaleInt()), lessThan(decimal(randomLong(positive()), randomScaleInt())));
        assertThat(decimal(0L, randomScaleInt()), lessThan(decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt())));

        assertThat(decimal(randomLong(positive()), randomScaleInt()), greaterThan(decimal(0L, randomScaleInt())));
        assertThat(decimal(randomLong(positive()), randomScaleInt()), greaterThan(decimal(randomLong(negative()), randomScaleInt())));
        assertThat(decimal(randomLong(positive()), randomScaleInt()), greaterThan(decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt())));

        assertThat(decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt()), greaterThan(decimal(0L, randomScaleInt())));
        assertThat(decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt()), greaterThan(decimal(randomLong(negative()), randomScaleInt())));
        assertThat(decimal(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt()), greaterThan(decimal(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomScaleInt())));
    }

    @Test
    public void shouldCompareDecimalsWithTheSameUnscaledValue() {
        long unscaledValueL;
        BigInteger unscaledValueB;

        // todo: uncomment - comparison of extreme scale difference for HugeDecimals is currently extremely slow
//        int scale = randomInt(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
//        int greaterScale = randomInt(scale + 1, Integer.MAX_VALUE);
//        int lowerScale = randomInt(Integer.MIN_VALUE, scale - 1);
        int scale = randomInt(-1_000, 1_000);
        int lowerScale = randomInt(scale - 1_000, scale - 1);
        int greaterScale = randomInt(scale + 1, scale + 1_000);

        unscaledValueL = randomLong(positive());
        assertThat(decimal(unscaledValueL, scale), comparesEqualTo(decimal(unscaledValueL, scale)));
        assertThat(decimal(unscaledValueL, scale), lessThan(decimal(unscaledValueL, lowerScale)));
        assertThat(decimal(unscaledValueL, scale), greaterThan(decimal(unscaledValueL, greaterScale)));

        unscaledValueL = randomLong(negative());
        assertThat(decimal(unscaledValueL, scale), comparesEqualTo(decimal(unscaledValueL, scale)));
        assertThat(decimal(unscaledValueL, scale), greaterThan(decimal(unscaledValueL, lowerScale)));
        assertThat(decimal(unscaledValueL, scale), lessThan(decimal(unscaledValueL, greaterScale)));

        assertThat(decimal(0L, scale), comparesEqualTo(decimal(0L, scale)));
        assertThat(decimal(0L, scale), comparesEqualTo(decimal(0L, greaterScale)));
        assertThat(decimal(0L, scale), comparesEqualTo(decimal(0L, lowerScale)));

        unscaledValueB = randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10());
        assertThat(decimal(unscaledValueB, scale), comparesEqualTo(decimal(unscaledValueB, scale)));
        assertThat(decimal(unscaledValueB, scale), lessThan(decimal(unscaledValueB, lowerScale)));
        assertThat(decimal(unscaledValueB, scale), greaterThan(decimal(unscaledValueB, greaterScale)));

        unscaledValueB = randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10());
        assertThat(decimal(unscaledValueB, scale), comparesEqualTo(decimal(unscaledValueB, scale)));
        assertThat(decimal(unscaledValueB, scale), greaterThan(decimal(unscaledValueB, lowerScale)));
        assertThat(decimal(unscaledValueB, scale), lessThan(decimal(unscaledValueB, greaterScale)));
    }

    @Test
    public void shouldCompareDecimalsWithTheSameScale() {
        int scale = randomInt();

        long valueL = randomLong(Long.MIN_VALUE + 1, Long.MAX_VALUE - 1);
        long lowerValueL = randomLong(Long.MIN_VALUE, valueL - 1);
        long greaterValueL = randomLong(valueL + 1, Long.MAX_VALUE);

        assertThat(decimal(valueL, scale), comparesEqualTo(decimal(valueL, scale)));
        assertThat(decimal(valueL, scale), greaterThan(decimal(lowerValueL, scale)));
        assertThat(decimal(valueL, scale), lessThan(decimal(greaterValueL, scale)));

        BigInteger valueB = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        BigInteger lowerValueB = valueB.subtract(BigInteger.valueOf(randomLong(positive())));
        BigInteger greaterValueB = valueB.add(BigInteger.valueOf(randomLong(positive())));

        assertThat(decimal(valueB, scale), comparesEqualTo(decimal(valueB, scale)));
        assertThat(decimal(valueB, scale), greaterThan(decimal(lowerValueB, scale)));
        assertThat(decimal(valueB, scale), lessThan(decimal(greaterValueB, scale)));
    }

    @Test
    public void shouldCompareDecimalsWithDifferentUnscaledValueAndScale() {
        int scaleA = randomScaleInt();
        int scaleB = randomScaleInt(otherThan(scaleA));

        BigInteger adjustedPowerOf10A = powerOf10((long) Math.max(scaleA, scaleB) - (long) scaleA);
        BigInteger adjustedPowerOf10B = powerOf10((long) Math.max(scaleA, scaleB) - (long) scaleB);

        long unscaledValueL1 = randomLong();
        long unscaledValueL2 = randomLong(otherThan(unscaledValueL1));
        BigInteger unscaledValueB1 = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        BigInteger unscaledValueB2 = randomBigInteger(otherThan(unscaledValueB1), notFitIntoLong(), notDivisibleBy10());

        assertThat(
                decimal(unscaledValueL1, scaleA)
                        .compareTo(decimal(unscaledValueL2, scaleB)),
                equalTo(BigInteger.valueOf(unscaledValueL1).multiply(adjustedPowerOf10A)
                        .compareTo(BigInteger.valueOf(unscaledValueL2).multiply(adjustedPowerOf10B)))
        );

        assertThat(
                decimal(unscaledValueL1, scaleA).compareTo(
                        decimal(unscaledValueB1, scaleB)),
                equalTo(BigInteger.valueOf(unscaledValueL1).multiply(adjustedPowerOf10A).compareTo(
                        unscaledValueB1.multiply(adjustedPowerOf10B)))
        );

        assertThat(
                decimal(unscaledValueB1, scaleA).compareTo(
                        decimal(unscaledValueB2, scaleB)),
                equalTo(unscaledValueB1.multiply(adjustedPowerOf10A).compareTo(
                        unscaledValueB2.multiply(adjustedPowerOf10B)))
        );
    }

    @SafeVarargs
    private static int randomScaleInt(Function<Integer, Boolean>... validityConditions) {
        // todo: uncomment - comparison of extreme scale difference for HugeDecimals is currently extremely slow
//        return randomInt();
        return randomInt(-1_000, 1_000);
    }

    private BigInteger powerOf10(long scale) {
        long remainingScale = scale;
        BigInteger result = BigInteger.ONE;
        while (remainingScale > 0) {
            if (remainingScale < Integer.MAX_VALUE) {
                result = result.multiply(BigInteger.TEN.pow((int) remainingScale));
                remainingScale = 0;
            } else {
                result = result.multiply(BigInteger.TEN.pow(Integer.MAX_VALUE));
                remainingScale -= Integer.MAX_VALUE;
            }
        }
        return result;
    }
}