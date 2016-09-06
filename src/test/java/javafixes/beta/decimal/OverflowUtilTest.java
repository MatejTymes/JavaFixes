package javafixes.beta.decimal;

import org.junit.Test;

import static javafixes.beta.decimal.OverflowUtil.*;
import static javafixes.test.Random.randomInt;
import static javafixes.test.Random.randomLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OverflowUtilTest {

    @Test
    public void shouldFindIfNegationWillOverflow() {
        assertThat(willNegationOverflow(Long.MIN_VALUE), is(true));

        assertThat(willNegationOverflow(randomLong(Long.MIN_VALUE + 1, Long.MAX_VALUE)), is(false));
    }

    @Test
    public void shouldFindIfAdditionOfLongsHasOverflown() {

        assertThat(didOverflowOnLongAddition(Long.MAX_VALUE + 3L, Long.MAX_VALUE, 3L), is(true));
        assertThat(didOverflowOnLongAddition(1L + Long.MAX_VALUE, 1L, Long.MAX_VALUE), is(true));
        assertThat(didOverflowOnLongAddition(Long.MAX_VALUE + Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE), is(true));
        assertThat(didOverflowOnLongAddition(Long.MIN_VALUE + -3L, Long.MIN_VALUE, -3L), is(true));
        assertThat(didOverflowOnLongAddition(-1L + Long.MIN_VALUE, -1L, Long.MIN_VALUE), is(true));
        assertThat(didOverflowOnLongAddition(Long.MIN_VALUE + Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE), is(true));
        assertThat(didOverflowOnLongAddition(11L + (Long.MAX_VALUE - 10L), 11L, (Long.MAX_VALUE - 10L)), is(true));
        assertThat(didOverflowOnLongAddition((Long.MIN_VALUE + 1000L) + -1001L, (Long.MIN_VALUE + 1000L), -1001L), is(true));

        assertThat(didOverflowOnLongAddition(Long.MAX_VALUE + 0L, Long.MAX_VALUE, 0L), is(false));
        assertThat(didOverflowOnLongAddition(0L + Long.MAX_VALUE, 0L, Long.MAX_VALUE), is(false));
        assertThat(didOverflowOnLongAddition(Long.MIN_VALUE + 0L, Long.MIN_VALUE, 0L), is(false));
        assertThat(didOverflowOnLongAddition(0L + Long.MIN_VALUE, 0L, Long.MIN_VALUE), is(false));
        assertThat(didOverflowOnLongAddition(Long.MAX_VALUE + -3L, Long.MAX_VALUE, -3L), is(false));
        assertThat(didOverflowOnLongAddition(-1L + Long.MAX_VALUE, -1L, Long.MAX_VALUE), is(false));
        assertThat(didOverflowOnLongAddition(Long.MIN_VALUE + 3L, Long.MIN_VALUE, 3L), is(false));
        assertThat(didOverflowOnLongAddition(1L + Long.MIN_VALUE, 1L, Long.MIN_VALUE), is(false));
        assertThat(didOverflowOnLongAddition(Long.MAX_VALUE + Long.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE), is(false));
        assertThat(didOverflowOnLongAddition(10L + (Long.MAX_VALUE - 10L), 10L, (Long.MAX_VALUE - 10L)), is(false));
        assertThat(didOverflowOnLongAddition((Long.MIN_VALUE + 1000L) + -1000L, (Long.MIN_VALUE + 1000L), -1000L), is(false));
    }

    @Test
    public void shouldFindIfAdditionOfIntegersHasOverflown() {

        assertThat(didOverflowOnIntAddition(Integer.MAX_VALUE + 3, Integer.MAX_VALUE, 3), is(true));
        assertThat(didOverflowOnIntAddition(1 + Integer.MAX_VALUE, 1, Integer.MAX_VALUE), is(true));
        assertThat(didOverflowOnIntAddition(Integer.MAX_VALUE + Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), is(true));
        assertThat(didOverflowOnIntAddition(Integer.MIN_VALUE + -3, Integer.MIN_VALUE, -3), is(true));
        assertThat(didOverflowOnIntAddition(-1 + Integer.MIN_VALUE, -1, Integer.MIN_VALUE), is(true));
        assertThat(didOverflowOnIntAddition(Integer.MIN_VALUE + Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE), is(true));
        assertThat(didOverflowOnIntAddition(11 + (Integer.MAX_VALUE - 10), 11, (Integer.MAX_VALUE - 10)), is(true));
        assertThat(didOverflowOnIntAddition((Integer.MIN_VALUE + 1000) + -1001, (Integer.MIN_VALUE + 1000), -1001), is(true));

        assertThat(didOverflowOnIntAddition(Integer.MAX_VALUE + 0, Integer.MAX_VALUE, 0), is(false));
        assertThat(didOverflowOnIntAddition(0 + Integer.MAX_VALUE, 0, Integer.MAX_VALUE), is(false));
        assertThat(didOverflowOnIntAddition(Integer.MIN_VALUE + 0, Integer.MIN_VALUE, 0), is(false));
        assertThat(didOverflowOnIntAddition(0 + Integer.MIN_VALUE, 0, Integer.MIN_VALUE), is(false));
        assertThat(didOverflowOnIntAddition(Integer.MAX_VALUE + -3, Integer.MAX_VALUE, -3), is(false));
        assertThat(didOverflowOnIntAddition(-1 + Integer.MAX_VALUE, -1, Integer.MAX_VALUE), is(false));
        assertThat(didOverflowOnIntAddition(Integer.MIN_VALUE + 3, Integer.MIN_VALUE, 3), is(false));
        assertThat(didOverflowOnIntAddition(1 + Integer.MIN_VALUE, 1, Integer.MIN_VALUE), is(false));
        assertThat(didOverflowOnIntAddition(Integer.MAX_VALUE + Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE), is(false));
        assertThat(didOverflowOnIntAddition(10 + (Integer.MAX_VALUE - 10), 10, (Integer.MAX_VALUE - 10)), is(false));
        assertThat(didOverflowOnIntAddition((Integer.MIN_VALUE + 1000) + -1000, (Integer.MIN_VALUE + 1000), -1000), is(false));
    }

    @Test
    public void shouldFindIfMultiplicationHasOverflown() {
        assertThat(didOverflowOnMultiplication(Long.MAX_VALUE * 2L, Long.MAX_VALUE, 2L), is(true));
        assertThat(didOverflowOnMultiplication(-2L * Long.MAX_VALUE, -2L, Long.MAX_VALUE), is(true));
        assertThat(didOverflowOnMultiplication(Long.MAX_VALUE * Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE), is(true));
        assertThat(didOverflowOnMultiplication(Long.MIN_VALUE * -1L, Long.MIN_VALUE, -1L), is(true));
        assertThat(didOverflowOnMultiplication(2L * Long.MIN_VALUE, 2L, Long.MIN_VALUE), is(true));
        assertThat(didOverflowOnMultiplication(-2L * Long.MIN_VALUE, -2L, Long.MIN_VALUE), is(true));
        assertThat(didOverflowOnMultiplication(Long.MIN_VALUE * Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE), is(true));
        assertThat(didOverflowOnMultiplication(Long.MIN_VALUE * Long.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE), is(true));

        assertThat(didOverflowOnMultiplication(Long.MAX_VALUE * 1L, Long.MAX_VALUE, 1L), is(false));
        assertThat(didOverflowOnMultiplication(Long.MAX_VALUE * 0L, Long.MAX_VALUE, 0L), is(false));
        assertThat(didOverflowOnMultiplication(0L * Long.MAX_VALUE, 0L, Long.MAX_VALUE), is(false));
        assertThat(didOverflowOnMultiplication(1L * Long.MIN_VALUE, 1L, Long.MIN_VALUE), is(false));
        assertThat(didOverflowOnMultiplication(-1L * Long.MAX_VALUE, -1L, Long.MAX_VALUE), is(false));
        assertThat(didOverflowOnMultiplication(394019L * -9321409L, 394019L, -9321409L), is(false));
    }

    @Test
    public void shouldFindIfLongCanBeCastToInt() {
        assertThat(canCastLongToInt((long) Integer.MIN_VALUE), is(true));
        assertThat(canCastLongToInt((long) Integer.MAX_VALUE), is(true));
        assertThat(canCastLongToInt((long) Integer.MAX_VALUE - 1L), is(true));
        assertThat(canCastLongToInt((long) Integer.MIN_VALUE + 1L), is(true));
        assertThat(canCastLongToInt((long) randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE)), is(true));

        assertThat(canCastLongToInt((long) Integer.MAX_VALUE + 1L), is(false));
        assertThat(canCastLongToInt((long) Integer.MIN_VALUE - 1L), is(false));
        assertThat(canCastLongToInt(randomLong(Long.MIN_VALUE, (long) Integer.MIN_VALUE - 1L)), is(false));
        assertThat(canCastLongToInt(randomLong((long) Integer.MAX_VALUE +1L, Long.MAX_VALUE)), is(false));
    }

}