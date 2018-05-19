package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.d;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalAdditionAndSubtractionTest {

    @Test
    public void shouldAddAndSubtractTwoDecimalsOfTheSameScale() {
        for (int scale : newList(
                0,
                -1,
                1,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                randomInt(Integer.MIN_VALUE + 1, -2),
                randomInt(2, Integer.MAX_VALUE - 1)
        )) {
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
                    verifyCanSumDecimals(decimalA, decimalB);
                    verifyCanSubtractDecimals(decimalA, decimalB);
                }
            }
        }
    }

    @Test
    public void shouldAddAndSubtractTwoDecimalsOfDifferentScale() {
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
                    for (Decimal decimalB : newList(
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
                        verifyCanSumDecimals(decimalA, decimalB);
                        verifyCanSubtractDecimals(decimalA, decimalB);
                    }
                }
            }
        }
    }

    // for BigDecimal this would take forever
//    @Test
//    public void bigDecimalTest() {
//        System.out.println("Decimal");
//        Decimal sumD = d("0.0").plus(d("-1e2147483648"));
//        System.out.println("BigDecimal");
//        BigDecimal sumBD = new BigDecimal("0").add(new BigDecimal("-1e214748364"));
//    }

    private void verifyCanSumDecimals(Decimal decimalA, Decimal decimalB) {
//        System.out.println(decimalA + " + " + decimalB);

        BigInteger unscaledValueA = decimalA.unscaledValueAsBigInteger();
        BigInteger unscaledValueB = decimalB.unscaledValueAsBigInteger();

        int scaleA = decimalA.scale();
        int scaleB = decimalB.scale();

        long expectedScale;
        BigInteger expectedUnscaledValueSum;
        if (decimalA.signum() == 0) {
            expectedScale = decimalB.scale();
            expectedUnscaledValueSum = decimalB.unscaledValueAsBigInteger();
        } else if (decimalB.signum() == 0) {
            expectedScale = decimalA.scale();
            expectedUnscaledValueSum = decimalA.unscaledValueAsBigInteger();
        } else if (scaleA == scaleB) {
            expectedScale = scaleA;
            expectedUnscaledValueSum = unscaledValueA.add(unscaledValueB);
        } else if (scaleA > scaleB) {
            expectedScale = scaleA;
            expectedUnscaledValueSum = unscaledValueA.add(
                    PowerUtil.upscaleByPowerOf10(unscaledValueB, (long) scaleA - scaleB)
            );
        } else {
            expectedScale = scaleB;
            expectedUnscaledValueSum = unscaledValueB.add(
                    PowerUtil.upscaleByPowerOf10(unscaledValueA, (long) scaleB - scaleA)
            );
        }

        while (expectedUnscaledValueSum.signum() != 0 && mod(expectedUnscaledValueSum, 10) == 0) {
            expectedUnscaledValueSum = expectedUnscaledValueSum.divide(BigInteger.TEN);
            expectedScale--;
        }


        if (LongUtil.canFitIntoInt(expectedScale)) {

            // When
            Decimal sum = decimalA.plus(decimalB);

            // Then
            assertThat(sum, equalTo(d(expectedUnscaledValueSum, (int) expectedScale)));

        } else {

            try {
                // When
                decimalA.plus(decimalB);

                // Then
                fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
            } catch (ArithmeticException expected) {
                assertThat(expected.getMessage(), equalTo("Scale underflow - can't set scale to less than '-2147483648'"));
            }
        }
    }

    private void verifyCanSubtractDecimals(Decimal decimalA, Decimal decimalB) {
//        System.out.println(decimalA + " - " + decimalB);

        BigInteger unscaledValueA = decimalA.unscaledValueAsBigInteger();
        BigInteger unscaledValueB = decimalB.unscaledValueAsBigInteger();

        int scaleA = decimalA.scale();
        int scaleB = decimalB.scale();

        long expectedScale;
        BigInteger expectedUnscaledValueSum;
        if (decimalA.signum() == 0) {
            expectedScale = decimalB.scale();
            expectedUnscaledValueSum = decimalB.unscaledValueAsBigInteger().negate();
        } else if (decimalB.signum() == 0) {
            expectedScale = decimalA.scale();
            expectedUnscaledValueSum = decimalA.unscaledValueAsBigInteger();
        } else if (scaleA == scaleB) {
            expectedScale = scaleA;
            expectedUnscaledValueSum = unscaledValueA.subtract(unscaledValueB);
        } else if (scaleA > scaleB) {
            expectedScale = scaleA;
            expectedUnscaledValueSum = unscaledValueA.subtract(
                    PowerUtil.upscaleByPowerOf10(unscaledValueB, (long) scaleA - scaleB)
            );
        } else {
            expectedScale = scaleB;
            expectedUnscaledValueSum = PowerUtil.upscaleByPowerOf10(unscaledValueA, (long) scaleB - scaleA).subtract(
                    unscaledValueB
            );
        }
        while (expectedUnscaledValueSum.signum() != 0 && mod(expectedUnscaledValueSum, 10) == 0) {
            expectedUnscaledValueSum = expectedUnscaledValueSum.divide(BigInteger.TEN);
            expectedScale--;
        }


        if (LongUtil.canFitIntoInt(expectedScale)) {

            // When
            Decimal sum = decimalA.minus(decimalB);

            // Then
            assertThat(sum, equalTo(d(expectedUnscaledValueSum, (int) expectedScale)));

        } else {

            try {
                // When
                decimalA.minus(decimalB);

                // Then
                fail("expecting ArithmeticException as scale can't be less than Long.MIN_VALUE");
            } catch (ArithmeticException expected) {
                assertThat(expected.getMessage(), equalTo("Scale underflow - can't set scale to less than '-2147483648'"));
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