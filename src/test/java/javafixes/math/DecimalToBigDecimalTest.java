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

public class DecimalToBigDecimalTest {

    private List<Integer> scales = newList(
            0,
            1,
            -1,
            Integer.MAX_VALUE,
            Integer.MIN_VALUE,
            randomInt(Integer.MIN_VALUE + 1, -2),
            randomInt(2, Integer.MAX_VALUE - 1)
    );

    @Test
    public void shouldBeAbleToTransformLongDecimalToBigDecimal() {
        List<Long> unscaledValues = newList(
                1L,
                -1L,
                Long.MAX_VALUE,
                Long.MIN_VALUE,
                randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10()),
                randomLong(2L, Long.MAX_VALUE - 1, notDivisibleBy10())
        );

        assertThat(Decimal.ZERO.bigDecimalValue().unscaledValue(), equalTo(BigInteger.ZERO));
        assertThat(Decimal.ZERO.bigDecimalValue().scale(), equalTo(0));

        for (int scale : scales) {
            for (long unscaledValue : unscaledValues) {
                Decimal decimal = Decimal.decimal(unscaledValue, scale);
                BigDecimal bigDecimal = decimal.bigDecimalValue();

                assertThat(bigDecimal.unscaledValue(), equalTo(BigInteger.valueOf(unscaledValue)));
                assertThat(bigDecimal.scale(), equalTo(scale));
            }
        }
    }

    @Test
    public void shouldBeAbleToTransformHugeDecimalToBigDecimal() {
        List<BigInteger> unscaledValues = newList(
                BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE),
                BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE),
                randomBigInteger(positive(), notDivisibleBy10(), notFitIntoLong()),
                randomBigInteger(negative(), notDivisibleBy10(), notFitIntoLong())
        );

        for (int scale : scales) {
            for (BigInteger unscaledValue : unscaledValues) {
                Decimal decimal = Decimal.decimal(unscaledValue, scale);
                BigDecimal bigDecimal = decimal.bigDecimalValue();

                assertThat(bigDecimal.unscaledValue(), equalTo(unscaledValue));
                assertThat(bigDecimal.scale(), equalTo(scale));
            }
        }
    }
}