package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigInteger;

import static mtymes.javafixes.beta.decimal.BigIntegerUtil.canConvertToLong;
import static mtymes.javafixes.test.Random.randomLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BigIntegerUtilTest {

    @Test
    public void shouldFindIfCanConvertBigIntegerToLong() {

        assertThat(canConvertToLong(BigInteger.valueOf(Long.MAX_VALUE)), is(true));
        assertThat(canConvertToLong(BigInteger.valueOf(Long.MIN_VALUE)), is(true));
        assertThat(canConvertToLong(BigInteger.valueOf(randomLong())), is(true));

        assertThat(canConvertToLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)), is(false));
        assertThat(canConvertToLong(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)), is(false));

        assertThat(canConvertToLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(randomLong(1, Long.MAX_VALUE)))), is(false));
        assertThat(canConvertToLong(BigInteger.valueOf(Long.MIN_VALUE).add(BigInteger.valueOf(randomLong(Long.MIN_VALUE, -1)))), is(false));
    }
}