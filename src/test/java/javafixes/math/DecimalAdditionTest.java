package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.d;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalAdditionTest {

    @Test
    public void shouldSumDecimalsOfTheSameScale() {
        List<Integer> scales = newList(
                0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                randomInt(Integer.MIN_VALUE + 1, -2),
                randomInt(2, Integer.MAX_VALUE - 1)
        );

        for (int scale : scales) {
            for (Decimal decimalA : newList(
                    d(0, scale),
                    d(-1, scale),
                    d(1, scale),
                    d(Long.MIN_VALUE, scale),
                    d(Long.MAX_VALUE, scale),
                    d(randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10()), scale),
                    d(randomLong(2L, Long.MAX_VALUE - 1, notDivisibleBy10()), scale),
                    d(randomBigInteger(notFitIntoLong(), negative(), notDivisibleBy10()), scale),
                    d(randomBigInteger(notFitIntoLong(), positive(), notDivisibleBy10()), scale)
            )) {
                for (Decimal decimalB : newList(
                        decimalA,
                        decimalA.negate(),
                        d(0, scale),
                        d(-1, scale),
                        d(1, scale),
                        d(Long.MIN_VALUE, scale),
                        d(Long.MAX_VALUE, scale),
                        d(randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10()), scale),
                        d(randomLong(2L, Long.MAX_VALUE - 1, notDivisibleBy10()), scale),
                        d(randomBigInteger(notFitIntoLong(), negative(), notDivisibleBy10()), scale),
                        d(randomBigInteger(notFitIntoLong(), positive(), notDivisibleBy10()), scale)
                )) {
                    BigInteger expectedUnscaledValueSum = decimalA.unscaledValueAsBigInteger()
                            .add(decimalB.unscaledValueAsBigInteger());
                    long expectedScale = decimalA.scale();
                    while(expectedUnscaledValueSum.signum() != 0 && mod(expectedUnscaledValueSum, 10) == 0) {
                        expectedUnscaledValueSum = expectedUnscaledValueSum.divide(BigInteger.TEN);
                        expectedScale--;
                    }

                    if (LongUtil.canFitIntoInt(expectedScale)) {

                        Decimal sum = decimalA.plus(decimalB);

                        assertThat(sum, equalTo(d(expectedUnscaledValueSum, (int) expectedScale)));

                    } else {

                        try {
                            decimalA.plus(decimalB);

                            fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
                        } catch (ArithmeticException expected) {
                            assertThat(expected.getMessage(), equalTo("Scale underflow - can't set scale to less than: -2147483648"));
                        }
                    }
                }
            }
        }
    }
}