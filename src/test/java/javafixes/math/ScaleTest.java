package javafixes.math;

import org.junit.Test;

import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ScaleTest {

    @Test
    public void shouldBeAbleToCreateScale() {
        assertThat(Scale.of(Integer.MIN_VALUE).value, equalTo(Integer.MIN_VALUE));
        assertThat(Scale.scale(Integer.MIN_VALUE).value, equalTo(Integer.MIN_VALUE));
        assertThat(Scale.of(Integer.MAX_VALUE).value, equalTo(Integer.MAX_VALUE));
        assertThat(Scale.scale(Integer.MAX_VALUE).value, equalTo(Integer.MAX_VALUE));
        assertThat(Scale.of(0).value, equalTo(0));
        assertThat(Scale.scale(0).value, equalTo(0));
        assertThat(Scale.of(-1).value, equalTo(-1));
        assertThat(Scale.scale(-1).value, equalTo(-1));
        assertThat(Scale.of(1).value, equalTo(1));
        assertThat(Scale.scale(1).value, equalTo(1));

        int positiveValue = randomInt(2, Integer.MAX_VALUE - 1);
        int negativeValue = randomInt(Integer.MIN_VALUE + 1, -2);
        assertThat(Scale.of(positiveValue).value, equalTo(positiveValue));
        assertThat(Scale.scale(positiveValue).value, equalTo(positiveValue));
        assertThat(Scale.of(negativeValue).value, equalTo(negativeValue));
        assertThat(Scale.scale(negativeValue).value, equalTo(negativeValue));
    }

    @Test
    public void shouldHaveWorkingEquals() {
        assertThat(Scale.of(Integer.MIN_VALUE), equalTo(Scale.scale(Integer.MIN_VALUE)));
        assertThat(Scale.of(Integer.MAX_VALUE), equalTo(Scale.scale(Integer.MAX_VALUE)));
        assertThat(Scale.of(0), equalTo(Scale.scale(0)));
        assertThat(Scale.of(-1), equalTo(Scale.scale(-1)));
        assertThat(Scale.of(1), equalTo(Scale.scale(1)));

        int positiveValue = randomInt(2, Integer.MAX_VALUE - 1);
        int negativeValue = randomInt(Integer.MIN_VALUE + 1, -2);
        assertThat(Scale.of(positiveValue), equalTo(Scale.scale(positiveValue)));
        assertThat(Scale.of(negativeValue), equalTo(Scale.scale(negativeValue)));
    }

    @Test
    public void shouldHaveWorkingHashCode() {
        assertThat(Scale.of(Integer.MIN_VALUE).hashCode(),equalTo(Scale.scale(Integer.MIN_VALUE).hashCode()));
        assertThat(Scale.of(Integer.MAX_VALUE).hashCode(),equalTo(Scale.scale(Integer.MAX_VALUE).hashCode()));
        assertThat(Scale.of(0).hashCode(),equalTo(Scale.scale(0).hashCode()));
        assertThat(Scale.of(-1).hashCode(),equalTo(Scale.scale(-1).hashCode()));
        assertThat(Scale.of(1).hashCode(),equalTo(Scale.scale(1).hashCode()));

        int positiveValue = randomInt(2, Integer.MAX_VALUE - 1);
        int negativeValue = randomInt(Integer.MIN_VALUE + 1, -2);
        assertThat(Scale.of(positiveValue).hashCode(),equalTo(Scale.scale(positiveValue).hashCode()));
        assertThat(Scale.of(negativeValue).hashCode(),equalTo(Scale.scale(negativeValue).hashCode()));
    }

    @Test
    public void shouldProvideHandyConstants() {
        assertThat(Scale._0_DECIMAL_PLACES.value, equalTo(0));
        assertThat(Scale._1_DECIMAL_PLACE.value, equalTo(1));
        assertThat(Scale._2_DECIMAL_PLACES.value, equalTo(2));
        assertThat(Scale._3_DECIMAL_PLACES.value, equalTo(3));
        assertThat(Scale._4_DECIMAL_PLACES.value, equalTo(4));
        assertThat(Scale._5_DECIMAL_PLACES.value, equalTo(5));
        assertThat(Scale._8_DECIMAL_PLACES.value, equalTo(8));
        assertThat(Scale._10_DECIMAL_PLACES.value, equalTo(10));
        assertThat(Scale.SCALE_OF_TENS.value, equalTo(-1));
        assertThat(Scale.SCALE_OF_HUNDREDS.value, equalTo(-2));
        assertThat(Scale.SCALE_OF_THOUSANDS.value, equalTo(-3));
        assertThat(Scale.SCALE_OF_MILLIONS.value, equalTo(-6));
    }
}