package javafixes.math;

import org.junit.Test;

import static javafixes.math.OverflowUtil.*;
import static javafixes.test.Random.randomLong;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OverflowUtilTest {

    @Test
    public void shouldFindIfNegationWillOverflow() {
        assertThat(willNegationOverflow(Long.MIN_VALUE), is(true));

        assertThat(willNegationOverflow(Long.MIN_VALUE + 1), is(false));
        assertThat(willNegationOverflow(Long.MAX_VALUE), is(false));
        assertThat(willNegationOverflow(1L), is(false));
        assertThat(willNegationOverflow(-1L), is(false));
        assertThat(willNegationOverflow(0L), is(false));

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
}