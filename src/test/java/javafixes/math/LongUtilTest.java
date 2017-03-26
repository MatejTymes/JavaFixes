package javafixes.math;

import org.junit.Test;

import static javafixes.test.Random.randomLong;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LongUtilTest {

    @Test
    public void shouldFindOutLongCanFitIntoInt() {
        assertThat(LongUtil.canFitIntoInt((long) Integer.MIN_VALUE), is(true));
        assertThat(LongUtil.canFitIntoInt((long) Integer.MAX_VALUE), is(true));
        assertThat(LongUtil.canFitIntoInt(-1L), is(true));
        assertThat(LongUtil.canFitIntoInt(0L), is(true));
        assertThat(LongUtil.canFitIntoInt(1L), is(true));
        assertThat(LongUtil.canFitIntoInt(randomLong(Integer.MIN_VALUE + 1, -2)), is(true));
        assertThat(LongUtil.canFitIntoInt(randomLong(2, Integer.MAX_VALUE - 1)), is(true));
    }

    @Test
    public void shouldFindOutLongCanNotFitIntoInt() {
        assertThat(LongUtil.canFitIntoInt(Long.MIN_VALUE), is(false));
        assertThat(LongUtil.canFitIntoInt(Long.MAX_VALUE), is(false));
        assertThat(LongUtil.canFitIntoInt((long) Integer.MIN_VALUE - 1), is(false));
        assertThat(LongUtil.canFitIntoInt((long) Integer.MAX_VALUE + 1), is(false));

        assertThat(LongUtil.canFitIntoInt(randomLong(Long.MIN_VALUE + 1, (long) Integer.MIN_VALUE - 2)), is(false));
        assertThat(LongUtil.canFitIntoInt(randomLong((long) Integer.MAX_VALUE + 2, Long.MAX_VALUE - 1)), is(false));
    }
}