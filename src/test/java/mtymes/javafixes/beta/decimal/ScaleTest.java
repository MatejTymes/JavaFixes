package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import static java.math.RoundingMode.DOWN;
import static mtymes.javafixes.beta.decimal.Decimal.d;
import static mtymes.javafixes.beta.decimal.Scale.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScaleTest {

    @Test
    public void shouldProvideCorrectScaleConstants() {

        assertThat(d("123.456789").descaleTo(SCALE_0, DOWN), is(d("123.0")));
        assertThat(d("123.456789").descaleTo(SCALE_1, DOWN), is(d("123.4")));
        assertThat(d("123.456789").descaleTo(SCALE_2, DOWN), is(d("123.45")));
        assertThat(d("123.456789").descaleTo(SCALE_3, DOWN), is(d("123.456")));
        assertThat(d("123.456789").descaleTo(SCALE_4, DOWN), is(d("123.4567")));
        assertThat(d("123.456789").descaleTo(SCALE_5, DOWN), is(d("123.45678")));

        assertThat(d("1.12345_12345_123").descaleTo(SCALE_8, DOWN), is(d("1.12345_123")));
        assertThat(d("1.12345_12345_123").descaleTo(SCALE_10, DOWN), is(d("1.12345_123_45")));

        assertThat(d("123_456_789.123").descaleTo(SCALE_OF_HUNDREDS, DOWN), is(d("123_456_700")));
        assertThat(d("123_456_789.123").descaleTo(SCALE_OF_THOUSANDS, DOWN), is(d("123_456_000")));
        assertThat(d("123_456_789.123").descaleTo(SCALE_OF_MILLIONS, DOWN), is(d("123_000_000")));

        assertThat(d("123.456789").descaleTo(_0_DECIMAL_PLACES, DOWN), is(d("123.0")));
        assertThat(d("123.456789").descaleTo(_1_DECIMAL_PLACE, DOWN), is(d("123.4")));
        assertThat(d("123.456789").descaleTo(_2_DECIMAL_PLACES, DOWN), is(d("123.45")));
        assertThat(d("123.456789").descaleTo(_3_DECIMAL_PLACES, DOWN), is(d("123.456")));
        assertThat(d("123.456789").descaleTo(_4_DECIMAL_PLACES, DOWN), is(d("123.4567")));
        assertThat(d("123.456789").descaleTo(_5_DECIMAL_PLACES, DOWN), is(d("123.45678")));

        assertThat(d("1.12345_12345_123").descaleTo(_8_DECIMAL_PLACES, DOWN), is(d("1.12345_123")));
        assertThat(d("1.12345_12345_123").descaleTo(_10_DECIMAL_PLACES, DOWN), is(d("1.12345_123_45")));
    }
}