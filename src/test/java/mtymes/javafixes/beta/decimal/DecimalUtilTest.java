package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import java.math.BigInteger;

import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalUtilTest {

    @Test
    public void shouldGetUnscaledValueFromLongDecimal() {
        long unscaledValue = randomLong();
        int scale = randomInt(-1_000, 1000);

        assertThat(DecimalUtil.bigUnscaledValueFrom(new LongDecimal(unscaledValue, scale)), equalTo(BigInteger.valueOf(unscaledValue)));
    }

    @Test
    public void shouldGetUnscaledValueFromHugeDecimal() {
        BigInteger unscaledValue = randomBigInteger();
        int scale = randomInt(-1_000, 1000);

        assertThat(DecimalUtil.bigUnscaledValueFrom(new Decimal.HugeDecimal(unscaledValue, scale)), equalTo(unscaledValue));
    }
}