package javafixes.math;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static javafixes.collection.CollectionUtil.newList;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.randomBigInteger;
import static javafixes.test.Random.randomLong;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalNumberConversionTest {

    @Test
    public void shouldTransformDecimalIntoPrimitiveType() {
        List<BigInteger> unscaledValues = newList(
                bi(0L),
                bi(-1L),
                bi(1L),
                bi(Long.MIN_VALUE),
                bi(Long.MAX_VALUE),
                bi(randomLong(Long.MIN_VALUE + 1, -2L, notDivisibleBy10())),
                bi(randomLong(2L, Long.MAX_VALUE -1L, notDivisibleBy10())),
                randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()),
                randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10())
        );

        for (BigInteger unscaledValue : unscaledValues) {
            assertThat(Decimal.decimal(unscaledValue, 0).intValue(), equalTo(unscaledValue.intValue()));
            assertThat(Decimal.decimal(unscaledValue, 0).longValue(), equalTo(unscaledValue.longValue()));
            assertThat(Decimal.decimal(unscaledValue, 0).floatValue(), equalTo(new BigDecimal(unscaledValue, 0).floatValue()));
            assertThat(Decimal.decimal(unscaledValue, 0).doubleValue(), equalTo(new BigDecimal(unscaledValue, 0).doubleValue()));

            for (int scale = 1; scale <= 1000; scale++) {
                BigInteger scaler = BigInteger.TEN.pow(scale);

                assertThat(Decimal.decimal(unscaledValue, scale).intValue(), equalTo(unscaledValue.divide(scaler).intValue()));
                assertThat(Decimal.decimal(unscaledValue, -scale).intValue(), equalTo(unscaledValue.multiply(scaler).intValue()));

                assertThat(Decimal.decimal(unscaledValue, scale).longValue(), equalTo(unscaledValue.divide(scaler).longValue()));
                assertThat(Decimal.decimal(unscaledValue, -scale).longValue(), equalTo(unscaledValue.multiply(scaler).longValue()));

                assertThat(Decimal.decimal(unscaledValue, scale).floatValue(), equalTo(new BigDecimal(unscaledValue, scale).floatValue()));
                assertThat(Decimal.decimal(unscaledValue, -scale).floatValue(), equalTo(new BigDecimal(unscaledValue, -scale).floatValue()));

                assertThat(Decimal.decimal(unscaledValue, scale).doubleValue(), equalTo(new BigDecimal(unscaledValue, scale).doubleValue()));
                assertThat(Decimal.decimal(unscaledValue, -scale).doubleValue(), equalTo(new BigDecimal(unscaledValue, -scale).doubleValue()));
            }
        }
    }

    private BigInteger bi(long value) {
        return BigInteger.valueOf(value);
    }
}
