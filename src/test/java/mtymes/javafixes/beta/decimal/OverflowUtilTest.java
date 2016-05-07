package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static mtymes.javafixes.beta.decimal.OverflowUtil.*;
import static mtymes.javafixes.test.Random.randomLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OverflowUtilTest {

    @Test
    public void shouldFindIfNegationWillOverflow() {
        assertThat(willNegationOverflow(MIN_VALUE), is(true));

        assertThat(willNegationOverflow(randomLong(MIN_VALUE + 1, MAX_VALUE)), is(false));
    }

    @Test
    public void shouldFindIfAdditionHasOverflown() {

        assertThat(hasAdditionOverflown(MAX_VALUE + 3L, MAX_VALUE, 3L), is(true));
        assertThat(hasAdditionOverflown(1L + MAX_VALUE, 1L, MAX_VALUE), is(true));
        assertThat(hasAdditionOverflown(MAX_VALUE + MAX_VALUE, MAX_VALUE, MAX_VALUE), is(true));
        assertThat(hasAdditionOverflown(MIN_VALUE + -3L, MIN_VALUE, -3L), is(true));
        assertThat(hasAdditionOverflown(-1L + MIN_VALUE, -1L, MIN_VALUE), is(true));
        assertThat(hasAdditionOverflown(MIN_VALUE + MIN_VALUE, MIN_VALUE, MIN_VALUE), is(true));
        assertThat(hasAdditionOverflown(11L + 9223372036854775797L, 11L, 9223372036854775797L), is(true));
        assertThat(hasAdditionOverflown(-9223372036854774808L + -1001L, -9223372036854774808L, -1001L), is(true));

        assertThat(hasAdditionOverflown(MAX_VALUE + 0L, MAX_VALUE, 0L), is(false));
        assertThat(hasAdditionOverflown(0L + MAX_VALUE, 0L, MAX_VALUE), is(false));
        assertThat(hasAdditionOverflown(MIN_VALUE + 0L, MIN_VALUE, 0L), is(false));
        assertThat(hasAdditionOverflown(0L + MIN_VALUE, 0L, MIN_VALUE), is(false));
        assertThat(hasAdditionOverflown(MAX_VALUE + -3L, MAX_VALUE, -3L), is(false));
        assertThat(hasAdditionOverflown(-1L + MAX_VALUE, -1L, MAX_VALUE), is(false));
        assertThat(hasAdditionOverflown(MIN_VALUE + 3L, MIN_VALUE, 3L), is(false));
        assertThat(hasAdditionOverflown(1L + MIN_VALUE, 1L, MIN_VALUE), is(false));
        assertThat(hasAdditionOverflown(MAX_VALUE + MIN_VALUE, MAX_VALUE, MIN_VALUE), is(false));
        assertThat(hasAdditionOverflown(10L + 9223372036854775797L, 10L, 9223372036854775797L), is(false));
        assertThat(hasAdditionOverflown(-9223372036854774808L + -1000L, -9223372036854774808L, -1000L), is(false));
    }

    @Test
    public void shouldFindIfMultiplicationHasOverflown() {
        assertThat(hasMultiplicationOverflown(MAX_VALUE * 2, MAX_VALUE, 2), is(true));
        assertThat(hasMultiplicationOverflown(-2 * MAX_VALUE, -2, MAX_VALUE), is(true));
        assertThat(hasMultiplicationOverflown(MAX_VALUE * MAX_VALUE, MAX_VALUE, MAX_VALUE), is(true));
        assertThat(hasMultiplicationOverflown(MIN_VALUE * -1, MIN_VALUE, -1), is(true));
        assertThat(hasMultiplicationOverflown(2 * MIN_VALUE, 2, MIN_VALUE), is(true));
        assertThat(hasMultiplicationOverflown(-2 * MIN_VALUE, -2, MIN_VALUE), is(true));
        assertThat(hasMultiplicationOverflown(MIN_VALUE * MIN_VALUE, MIN_VALUE, MIN_VALUE), is(true));
        assertThat(hasMultiplicationOverflown(MIN_VALUE * MAX_VALUE, MIN_VALUE, MAX_VALUE), is(true));

        assertThat(hasMultiplicationOverflown(MAX_VALUE * 1, MAX_VALUE, 1), is(false));
        assertThat(hasMultiplicationOverflown(-1 * MAX_VALUE, -1, MAX_VALUE), is(false));
        assertThat(hasMultiplicationOverflown(1 * MIN_VALUE, 1, MIN_VALUE), is(false));
        assertThat(hasMultiplicationOverflown(394019L * -9321409L, 394019L, -9321409L), is(false));
    }

}