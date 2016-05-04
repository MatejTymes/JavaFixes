package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import static mtymes.javafixes.beta.decimal.Decimal.d;
import static mtymes.javafixes.beta.decimal.Decimal.decimal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

// todo: add BigDecimal comparison test - check all operations
public class InternalDecimalTest {

    @Test
    public void shouldRemoveTrailingZerosOnCreation() {
        assertThat(d("-12345").unscaledValue(), equalTo(-12345L));
        assertThat(d("-12345").scale(), equalTo(0));

        assertThat(d("14.53000").unscaledValue(), equalTo(1453L));
        assertThat(d("14.53000").scale(), equalTo(2));

        assertThat(d("0.0000352").unscaledValue(), equalTo(352L));
        assertThat(d("0.0000352").scale(), equalTo(7));

        assertThat(d("710200").unscaledValue(), equalTo(7102L));
        assertThat(d("710200").scale(), equalTo(-2));

        assertThat(d("9100400000.00").unscaledValue(), equalTo(91004L));
        assertThat(d("9100400000.00").scale(), equalTo(-5));

        assertThat(d("-0038.00").unscaledValue(), equalTo(-38L));
        assertThat(d("-0038.00").scale(), equalTo(0));

        // todo: speed up this scenario
        assertThat(d("-3800000000000000000000000000.0").unscaledValue(), equalTo(-38L));
        assertThat(d("-3800000000000000000000000000.0").scale(), equalTo(-26));

        // todo: implement this
//        assertThat(d("-3800000000000000000000000100.0").unscaledValue(), equalTo(new BigInteger("38000000000000000000000001")));
//        assertThat(d("-3800000000000000000000000100.0").scale(), equalTo(-2));
    }

    @Test
    public void shouldTransitionFromLongIntoHugeDecimal() {
        Decimal longDecimal = decimal(Long.MIN_VALUE, 1);
        assertThat(longDecimal, instanceOf(LongDecimal.class));
        assertThat(longDecimal.toPlainString(), equalTo("-922337203685477580.8"));

        Decimal hugeDecimal = longDecimal.negate();
        assertThat(hugeDecimal, instanceOf(HugeDecimal.class));
        assertThat(hugeDecimal.toPlainString(), equalTo("922337203685477580.8"));
    }

    @Test
    public void shouldTransitionFromHugeIntoLongDecimal() {
        Decimal hugeDecimal = Decimal.decimal("922337203685477580.8");
        assertThat(hugeDecimal, instanceOf(HugeDecimal.class));

        Decimal longDecimal = hugeDecimal.negate();
        assertThat(longDecimal, instanceOf(LongDecimal.class));
        assertThat(longDecimal, equalTo(decimal(Long.MIN_VALUE, 1)));
    }

}