package javafixes.math;

import org.junit.Test;

import java.math.MathContext;
import java.math.RoundingMode;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.d;
import static javafixes.math.Precision.precision;
import static javafixes.math.Scale.scale;
import static javafixes.test.Condition.notDivisibleBy10;
import static javafixes.test.Random.randomLong;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalDivisionTest {

    @Test
    public void shouldDivideDecimals() {
        for (int signA : newList(1, -1)) {
            for (int signB : newList(1, -1)) {
                for (int scaleA = -20; scaleA <= 20; scaleA++) {
                    for (int scaleB = -20; scaleB <= 20; scaleB++) {
                        for (Decimal valueA : newList(
                                Decimal.ZERO,
                                d(randomLong(1, 999, notDivisibleBy10()) * signA, scaleA),
                                d(randomLong(1_000, 999_999, notDivisibleBy10()) * signA, scaleA),
                                d(randomLong(1_000_000, 999_999_999, notDivisibleBy10()) * signA, scaleA)
                        )) {
                            for (Decimal valueB : newList(
                                    d(randomLong(1, 999, notDivisibleBy10()) * signB, scaleB),
                                    d(randomLong(1_000, 999_999, notDivisibleBy10()) * signB, scaleB),
                                    d(randomLong(1_000_000, 999_999_999, notDivisibleBy10()) * signB, scaleB)
                            )) {
                                for (RoundingMode roundingMode : RoundingMode.values()) {
                                    if (roundingMode == RoundingMode.UNNECESSARY) {
                                        // todo: do a separate test for this rounding
                                        continue;
                                    }
                                    for (int scaleToUse  : newList(1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7, 8, -8, 9, -9, 10, -10, 15, -15, 20, -20, 25, -25, 30, -30, 40, -40, 50, -50)) {
                                        Decimal actualResult = valueA.div(valueB, scale(scaleToUse), roundingMode);

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
                                    for (int precisionToUse : newList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50)) {
                                        Decimal actualResult = valueA.div(valueB, precision(precisionToUse), roundingMode);

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
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    // todo: add division by zero test

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
