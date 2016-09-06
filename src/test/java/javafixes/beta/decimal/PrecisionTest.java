package javafixes.beta.decimal;

import org.junit.Test;

import static java.math.RoundingMode.DOWN;
import static javafixes.beta.decimal.Decimal.d;
import static javafixes.beta.decimal.Precision.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PrecisionTest {

    @Test
    public void shouldProvideCorrectScaleConstants() {

        assertThat(d("123456.789").deprecisionTo(PRECISION_7, DOWN), is(d("123456.7")));
        assertThat(d("123456789_0123456789_01").deprecisionTo(PRECISION_16, DOWN), is(d("123456789_0123456000_00")));
        assertThat(d("123456789_0123.456789_0123456789_0123456789").deprecisionTo(PRECISION_34, DOWN), is(d("123456789_0123.456789_0123456789_01234")));

        assertThat(d("123456.789").deprecisionTo(_7_SIGNIFICANT_DIGITS, DOWN), is(d("123456.7")));
        assertThat(d("123456789_0123456789_01").deprecisionTo(_16_SIGNIFICANT_DIGITS, DOWN), is(d("123456789_0123456000_00")));
        assertThat(d("123456789_0123.456789_0123456789_0123456789").deprecisionTo(_34_SIGNIFICANT_DIGITS, DOWN), is(d("123456789_0123.456789_0123456789_01234")));
    }
}