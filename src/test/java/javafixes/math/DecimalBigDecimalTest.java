package javafixes.math;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalBigDecimalTest {

    private List<Integer> scales = newList(
            0,
            1,
            -1,
            Integer.MAX_VALUE,
            Integer.MIN_VALUE,
            randomInt(-9, -2),
            randomInt(-20, -10),
            randomInt(Integer.MIN_VALUE + 1, -21),
            randomInt(2, 9),
            randomInt(10, 20),
            randomInt(21, Integer.MAX_VALUE - 1)
    );

    @Test
    public void shouldBeAbleToTransformLongDecimalToBigDecimalAndBack() {

        BigDecimal zeroBigDecimal = Decimal.ZERO.bigDecimalValue();
        assertThat(zeroBigDecimal.unscaledValue(), equalTo(BigInteger.ZERO));
        assertThat(zeroBigDecimal.scale(), equalTo(1));

        assertThat(Decimal.decimal(zeroBigDecimal), equalTo(Decimal.ZERO));
        assertThat(Decimal.decimal(zeroBigDecimal).unscaledValue(), equalTo(0L));
        assertThat(Decimal.decimal(zeroBigDecimal).scale(), equalTo(0));

        BigDecimal zeroScientificBigDecimal = Decimal.ZERO.bigDecimalScientificValue();
        assertThat(zeroScientificBigDecimal.unscaledValue(), equalTo(BigInteger.ZERO));
        assertThat(zeroScientificBigDecimal.scale(), equalTo(0));

        assertThat(Decimal.decimal(zeroScientificBigDecimal), equalTo(Decimal.ZERO));
        assertThat(Decimal.decimal(zeroScientificBigDecimal).unscaledValue(), equalTo(0L));
        assertThat(Decimal.decimal(zeroScientificBigDecimal).scale(), equalTo(0));

        List<Long> unscaledValues = newList(
                1L,
                -1L,
                Long.MAX_VALUE,
                Long.MIN_VALUE,
                randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10()),
                randomLong(2L, Long.MAX_VALUE - 1, notDivisibleBy10())
        );

        for (int scale : scales) {
            for (long unscaledValue : unscaledValues) {
                Decimal decimal = Decimal.decimal(unscaledValue, scale);

                BigDecimal scientificBigDecimal = decimal.bigDecimalScientificValue();
                BigDecimal bigDecimal = decimal.bigDecimalValue();

                Decimal newDecimal = Decimal.decimal(scientificBigDecimal);
                assertThat(newDecimal, equalTo(decimal));
                assertThat(newDecimal.unscaledValue(), equalTo(unscaledValue));
                assertThat(newDecimal.scale(), equalTo(scale));
                assertThat(Decimal.decimal(scientificBigDecimal), equalTo(Decimal.d(scientificBigDecimal)));

                newDecimal = Decimal.decimal(bigDecimal);
                assertThat(newDecimal, equalTo(decimal));
                assertThat(newDecimal.unscaledValue(), equalTo(unscaledValue));
                assertThat(newDecimal.scale(), equalTo(scale));
                assertThat(Decimal.decimal(bigDecimal), equalTo(Decimal.d(bigDecimal)));

                assertThat(scientificBigDecimal.unscaledValue(), equalTo(BigInteger.valueOf(unscaledValue)));
                assertThat(scientificBigDecimal.scale(), equalTo(scale));

                if (scale <= -9 || (scale > 9 && scale - decimal.precision() >= 9)) {
                    assertThat(bigDecimal.unscaledValue(), equalTo(BigInteger.valueOf(unscaledValue)));
                    assertThat(bigDecimal.scale(), equalTo(scale));
                } else {
                    int expectedScale = scale;
                    BigInteger expectedUnscaledValue = BigInteger.valueOf(unscaledValue);
                    if (expectedScale < 1) {
                        expectedUnscaledValue = expectedUnscaledValue.multiply(BigInteger.TEN.pow(1 - expectedScale));
                        expectedScale = 1;
                    }
                    assertThat(bigDecimal.unscaledValue(), equalTo(expectedUnscaledValue));
                    assertThat(bigDecimal.scale(), equalTo(expectedScale));
                }
            }
        }
    }

    @Test
    public void shouldBeAbleToTransformHugeDecimalToBigDecimalAndBack() {
        List<BigInteger> unscaledValues = newList(
                BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE),
                BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE),
                randomBigInteger(positive(), notDivisibleBy10(), notFitIntoLong()),
                randomBigInteger(negative(), notDivisibleBy10(), notFitIntoLong())
        );

        for (int scale : scales) {
            for (BigInteger unscaledValue : unscaledValues) {
                Decimal decimal = Decimal.decimal(unscaledValue, scale);

                BigDecimal scientificBigDecimal = decimal.bigDecimalScientificValue();
                BigDecimal bigDecimal = decimal.bigDecimalValue();

                Decimal newDecimal = Decimal.decimal(scientificBigDecimal);
                assertThat(newDecimal, equalTo(decimal));
                assertThat(newDecimal.unscaledValue(), equalTo(unscaledValue));
                assertThat(newDecimal.scale(), equalTo(scale));
                assertThat(Decimal.decimal(bigDecimal), equalTo(Decimal.d(bigDecimal)));

                newDecimal = Decimal.decimal(bigDecimal);
                assertThat(newDecimal, equalTo(decimal));
                assertThat(newDecimal.unscaledValue(), equalTo(unscaledValue));
                assertThat(newDecimal.scale(), equalTo(scale));
                assertThat(Decimal.decimal(bigDecimal), equalTo(Decimal.d(bigDecimal)));

                assertThat(scientificBigDecimal.unscaledValue(), equalTo(unscaledValue));
                assertThat(scientificBigDecimal.scale(), equalTo(scale));

                if (scale <= -9 || (scale > 9 && scale - decimal.precision() >= 9)) {
                    assertThat(bigDecimal.unscaledValue(), equalTo(unscaledValue));
                    assertThat(bigDecimal.scale(), equalTo(scale));
                } else {
                    int expectedScale = scale;
                    BigInteger expectedUnscaledValue = unscaledValue;
                    if (expectedScale < 1) {
                        expectedUnscaledValue = expectedUnscaledValue.multiply(BigInteger.TEN.pow(1 - expectedScale));
                        expectedScale = 1;
                    }
                    assertThat(bigDecimal.unscaledValue(), equalTo(expectedUnscaledValue));
                    assertThat(bigDecimal.scale(), equalTo(expectedScale));
                }
            }
        }
    }
}