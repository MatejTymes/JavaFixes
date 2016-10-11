package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static javafixes.test.Condition.notDivisibleBy10;
import static javafixes.test.Condition.notFitIntoLong;
import static javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class DecimalTest {

    @Test
    public void shouldCreateLongDecimalFromLong() {
        long unscaledValue = randomLong(notDivisibleBy10());
        int scale = randomInt();

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));
    }

    @Test
    public void shouldCreateHugeDecimalFromBigInteger() {
        BigInteger unscaledValue = randomBigInteger(notFitIntoLong(), notDivisibleBy10());
        int scale = randomInt();

        // When
        Decimal decimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));

        // When
        decimal = Decimal.d(unscaledValue, scale);

        // Then
        assertThat(decimal, instanceOf(Decimal.HugeDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(unscaledValue));
        assertThat(decimal.scale(), equalTo(scale));
    }


}