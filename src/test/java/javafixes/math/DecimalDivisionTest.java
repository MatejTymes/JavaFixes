package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;
import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.ZERO;
import static javafixes.math.Decimal.d;
import static javafixes.math.Precision._34_SIGNIFICANT_DIGITS;
import static javafixes.math.Precision.precision;
import static javafixes.math.Scale.scale;
import static javafixes.test.Condition.notDivisibleBy10;
import static javafixes.test.Condition.notFitIntoLong;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalDivisionTest {

    @Test
    public void shouldDivideDecimals() {
        List<Integer> signs = newList(1, -1);
        List<Integer> scales = newList(1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 7, -7, 10, -10, 15, -15, 20, -20, 25, -25, 30, -30, 40, -40, 50, -50);
        List<Integer> precisions = newList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50);

        for (int signA : signs) {
            for (int signB : signs) {
                for (int scaleA : scales) {
                    for (int scaleB : scales) {
                        for (Decimal valueA : newList(
                                Decimal.ZERO,
                                d(randomLong(1, 999, notDivisibleBy10()) * signA, scaleA),
                                d(randomLong(1_000, 999_999, notDivisibleBy10()) * signA, scaleA),
                                d(randomLong(1_000_000, 999_999_999, notDivisibleBy10()) * signA, scaleA),
                                d(randomBigInteger(notFitIntoLong(), notDivisibleBy10()).multiply(BigInteger.valueOf(signA)), scaleA)
                        )) {
                            for (Decimal valueB : newList(
                                    d(randomLong(1, 999, notDivisibleBy10()) * signB, scaleB),
                                    d(randomLong(1_000, 999_999, notDivisibleBy10()) * signB, scaleB),
                                    d(randomLong(1_000_000, 999_999_999, notDivisibleBy10()) * signB, scaleB),
                                    d(randomBigInteger(notFitIntoLong(), notDivisibleBy10()).multiply(BigInteger.valueOf(signB)), scaleB)
                            )) {

                                for (int scaleToUse : scales) {
                                    Scale scale = scale(scaleToUse);

                                    for (RoundingMode roundingMode : RoundingMode.values()) {
                                        if (roundingMode == RoundingMode.UNNECESSARY) {
                                            // todo: do a separate test for this rounding
                                            continue;
                                        }

                                        Decimal actualResult = valueA.div(valueB, scale, roundingMode);

                                        Decimal expectedValue = d(valueA.bigDecimalValue()
                                                .divide(
                                                        valueB.bigDecimalValue(),
                                                        scaleToUse,
                                                        roundingMode
                                                ));
                                        assertThat(
                                                valueA + " / " + valueB + "   [scale=" + scaleToUse + ", roundingMode=" + roundingMode + "]",
                                                actualResult,
                                                equalTo(expectedValue)
                                        );
                                    }

                                    assertThat(valueA.div(valueB, scale), equalTo(valueA.div(valueB, scale, HALF_UP)));
                                }

                                for (int precisionToUse : precisions) {
                                    Precision precision = precision(precisionToUse);

                                    for (RoundingMode roundingMode : RoundingMode.values()) {
                                        if (roundingMode == RoundingMode.UNNECESSARY) {
                                            // todo: do a separate test for this rounding
                                            continue;
                                        }
                                        Decimal actualResult = valueA.div(valueB, precision, roundingMode);

                                        Decimal expectedValue = d(valueA.bigDecimalValue()
                                                .divide(
                                                        valueB.bigDecimalValue(),
                                                        new MathContext(
                                                                precisionToUse,
                                                                roundingMode
                                                        )
                                                ));
                                        assertThat(
                                                valueA + " / " + valueB + "   [precision=" + precisionToUse + ", roundingMode=" + roundingMode + "]",
                                                actualResult,
                                                equalTo(expectedValue)
                                        );
                                    }

                                    assertThat(valueA.div(valueB, precision), equalTo(valueA.div(valueB, precision, HALF_UP)));
                                }
                                assertThat(valueA.div(valueB), equalTo(valueA.div(valueB, _34_SIGNIFICANT_DIGITS, HALF_UP)));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void shouldFailOnDivisionByZero() {
        try {
            d(randomLong(notDivisibleBy10()), randomInt()).div(ZERO);
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }
        try {
            d(randomBigInteger(notDivisibleBy10()), randomInt()).div(ZERO);
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }

        try {
            d(randomLong(notDivisibleBy10()), randomInt()).div(ZERO, Scale.of(randomInt()));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }
        try {
            d(randomBigInteger(notDivisibleBy10()), randomInt()).div(ZERO, Scale.of(randomInt()));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }

        try {
            d(randomLong(notDivisibleBy10()), randomInt()).div(ZERO, Precision.of(randomInt(1, Integer.MAX_VALUE)));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }
        try {
            d(randomBigInteger(notDivisibleBy10()), randomInt()).div(ZERO, Precision.of(randomInt(1, Integer.MAX_VALUE)));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }

        try {
            d(randomLong(notDivisibleBy10()), randomInt()).div(ZERO, Scale.of(randomInt()), pickRandomValue(RoundingMode.values()));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }
        try {
            d(randomBigInteger(notDivisibleBy10()), randomInt()).div(ZERO, Scale.of(randomInt()), pickRandomValue(RoundingMode.values()));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }

        try {
            d(randomLong(notDivisibleBy10()), randomInt()).div(ZERO, Precision.of(randomInt(1, Integer.MAX_VALUE)), pickRandomValue(RoundingMode.values()));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }
        try {
            d(randomBigInteger(notDivisibleBy10()), randomInt()).div(ZERO, Precision.of(randomInt(1, Integer.MAX_VALUE)), pickRandomValue(RoundingMode.values()));
            fail("Division by zero - expected ArithmeticException");
        } catch (ArithmeticException expected) {
            assertThat(expected.getMessage(), equalTo("Division by zero not allowed"));
        }
    }


    //    @Test
//    public void temp() {
//        Decimal valueA = d("3.52e22");
//        Decimal valueB = d("-8.8e21");
//
//        RoundingMode roundingMode = RoundingMode.UP;
//
//
//        Scale scaleToUse = Scale.of(-20);
//
//        Decimal actual = valueA.div(valueB, scaleToUse, roundingMode);
//        Decimal expected = d(valueA.bigDecimalValue().divide(valueB.bigDecimalValue(), scaleToUse.value, roundingMode));
//
//        assertThat(actual, equalTo(expected));
//
//
//        for (int precision = 1; precision < 20; precision++) {
//            Precision precisionToUse = Precision.of(precision);
//
//            actual = valueA.div(valueB, precisionToUse, roundingMode);
//            expected = d(valueA.bigDecimalValue().divide(valueB.bigDecimalValue(), new MathContext(precisionToUse.value, roundingMode)));
//
//            assertThat(actual, equalTo(expected));
//            System.out.println(actual);
//        }
//    }

}
