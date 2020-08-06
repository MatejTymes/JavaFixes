package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.collection.CollectionUtil.newList;
import static javafixes.math.Decimal.d;
import static javafixes.math.LongUtil.canFitIntoInt;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalMultiplicationTest {

    @Test
    public void shouldMultiplyTwoDecimals() {
        for (int scaleA : newList(
                0,
                -1,
                1,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                randomInt(Integer.MIN_VALUE + 1, -2),
                randomInt(2, Integer.MAX_VALUE - 1)
        )) {
            for (int scaleB : newList(
                    0,
                    -1,
                    1,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE,
                    randomInt(Integer.MIN_VALUE + 1, -2),
                    randomInt(2, Integer.MAX_VALUE - 1),
                    scalePlus(scaleA, randomInt(1, 9)), // huge scale differences would make the test slow - same goes for BigDecimal
                    scaleMinus(scaleA, randomInt(1, 9)), // huge scale differences would make the test slow - same goes for BigDecimal
                    scalePlus(scaleA, randomInt(10, 99)), // huge scale differences would make the test slow - same goes for BigDecimal
                    scaleMinus(scaleA, randomInt(10, 99)), // huge scale differences would make the test slow - same goes for BigDecimal
                    scalePlus(scaleA, randomInt(100, 999)), // huge scale differences would make the test slow - same goes for BigDecimal
                    scaleMinus(scaleA, randomInt(100, 999)) // huge scale differences would make the test slow - same goes for BigDecimal
            )) {
                for (Decimal decimalA : newList(
                        d(0, scaleA),
                        d(-1, scaleA),
                        d(1, scaleA),
                        d(Long.MIN_VALUE, scaleA),
                        d(Long.MAX_VALUE, scaleA),
                        d(randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10()), scaleA),
                        d(randomLong(2L, Long.MAX_VALUE - 1, notDivisibleBy10()), scaleA),
                        d(randomBigInteger(notFitIntoLong(), negative(), notDivisibleBy10()), scaleA),
                        d(randomBigInteger(notFitIntoLong(), positive(), notDivisibleBy10()), scaleA)
                )) {
                    int realScaleA = decimalA.scale();

                    for (Decimal decimalB : newList(
                            decimalA,
                            decimalA.negate(),
                            d(decimalA.unscaledValueAsBigInteger(), realScaleA),
                            d(decimalA.unscaledValueAsBigInteger().negate(), realScaleA),
                            d(decimalA.unscaledValueAsBigInteger().add(BigInteger.TEN), realScaleA),
                            d(decimalA.unscaledValueAsBigInteger().subtract(BigInteger.TEN), realScaleA),
                            d(decimalA.unscaledValueAsBigInteger().negate().add(BigInteger.TEN), realScaleA),
                            d(decimalA.unscaledValueAsBigInteger().negate().subtract(BigInteger.TEN), realScaleA),
                            d(decimalA.unscaledValueAsBigInteger(), scaleB),
                            d(decimalA.unscaledValueAsBigInteger().negate(), scaleB),
                            d(0, scaleB),
                            d(-1, scaleB),
                            d(1, scaleB),
                            d(Long.MIN_VALUE, scaleB),
                            d(Long.MAX_VALUE, scaleB),
                            d(randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10()), scaleB),
                            d(randomLong(2L, Long.MAX_VALUE - 1, notDivisibleBy10()), scaleB),
                            d(randomBigInteger(notFitIntoLong(), negative(), notDivisibleBy10()), scaleB),
                            d(randomBigInteger(notFitIntoLong(), positive(), notDivisibleBy10()), scaleB)
                    )) {
                        verifyCanMultiplyDecimals(decimalA, decimalB);
                    }
                }
            }
        }
    }

    private void verifyCanMultiplyDecimals(Decimal decimalA, Decimal decimalB) {
//        System.out.println(decimalA + " * " + decimalB);

        BigInteger unscaledValueA = decimalA.unscaledValueAsBigInteger();
        BigInteger unscaledValueB = decimalB.unscaledValueAsBigInteger();

        int scaleA = decimalA.scale();
        int scaleB = decimalB.scale();

        BigInteger expectedUnscaledValueSum = unscaledValueA.multiply(unscaledValueB);
        long expectedScale;
        if (expectedUnscaledValueSum.signum() == 0) {
            expectedScale = 0L;
        } else {
            expectedScale = (long) scaleA + (long) scaleB;
        }

        if (!canFitIntoInt(expectedScale)) {
            // todo: maybe we could improve this situation
            try {
                // When
                decimalA.times(decimalB);

                // Then
                fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
            } catch (ArithmeticException expected) {
                if (expectedScale < 0) {
                    assertThat(expected.getMessage(), equalTo("Scale underflow - multiplication resolves into non-integer scale '" + expectedScale + "'"));
                } else {
                    assertThat(expected.getMessage(), equalTo("Scale overflow - multiplication resolves into non-integer scale '" + expectedScale + "'"));
                }
            }

            try {
                // When
                decimalA.multiply(decimalB);

                // Then
                fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
            } catch (ArithmeticException expected) {
                if (expectedScale < 0) {
                    assertThat(expected.getMessage(), equalTo("Scale underflow - multiplication resolves into non-integer scale '" + expectedScale + "'"));
                } else {
                    assertThat(expected.getMessage(), equalTo("Scale overflow - multiplication resolves into non-integer scale '" + expectedScale + "'"));
                }
            }
        } else {

            while (expectedUnscaledValueSum.signum() != 0 && mod(expectedUnscaledValueSum, 10) == 0) {
                expectedUnscaledValueSum = expectedUnscaledValueSum.divide(BigInteger.TEN);
                expectedScale--;
            }

            if (canFitIntoInt(expectedScale)) {

                // When
                Decimal sum = decimalA.times(decimalB);
                // Then
                assertThat(sum, equalTo(d(expectedUnscaledValueSum, (int) expectedScale)));

                // When
                sum = decimalA.multiply(decimalB);
                // Then
                assertThat(sum, equalTo(d(expectedUnscaledValueSum, (int) expectedScale)));

            } else {

                try {
                    // When
                    decimalA.times(decimalB);

                    // Then
                    fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
                } catch (ArithmeticException expected) {
                    assertThat(expected.getMessage(), equalTo("Scale underflow - can't set scale to less than '-2147483648'"));
                }

                try {
                    // When
                    decimalA.multiply(decimalB);

                    // Then
                    fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
                } catch (ArithmeticException expected) {
                    assertThat(expected.getMessage(), equalTo("Scale underflow - can't set scale to less than '-2147483648'"));
                }
            }
        }
    }


    private int scalePlus(int scaleA, int value) {
        long newScale = (long) scaleA + value;
        return (newScale > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) newScale;
    }

    private int scaleMinus(int scaleA, int value) {
        long newScale = (long) scaleA - value;
        return (newScale < Integer.MIN_VALUE) ? Integer.MIN_VALUE : (int) newScale;
    }
}
