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

        assertThat(d("123.456789").descaleTo(Scale_0, DOWN), is(d("123.0")));
        assertThat(d("123.456789").descaleTo(Scale_1, DOWN), is(d("123.4")));
        assertThat(d("123.456789").descaleTo(Scale_2, DOWN), is(d("123.45")));
        assertThat(d("123.456789").descaleTo(Scale_3, DOWN), is(d("123.456")));
        assertThat(d("123.456789").descaleTo(Scale_4, DOWN), is(d("123.4567")));
        assertThat(d("123.456789").descaleTo(Scale_5, DOWN), is(d("123.45678")));

        assertThat(d("1.12345_12345_123").descaleTo(Scale_8, DOWN), is(d("1.12345_123")));
        assertThat(d("1.12345_12345_123").descaleTo(Scale_10, DOWN), is(d("1.12345_123_45")));

        assertThat(d("123_456_789.123").descaleTo(Scale_Of_Hundreds, DOWN), is(d("123_456_700")));
        assertThat(d("123_456_789.123").descaleTo(Scale_Of_Thousands, DOWN), is(d("123_456_000")));
        assertThat(d("123_456_789.123").descaleTo(Scale_Of_Millions, DOWN), is(d("123_000_000")));
    }
}