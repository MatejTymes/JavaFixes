package javafixes.math;

import org.junit.Test;

import static javafixes.collection.util.CollectionUtil.newList;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PrecisionTest {

    @Test
    public void shouldCreatePrecisionWithPositiveValue() {

        assertThat(Precision.of(1).value, equalTo(1));
        assertThat(Precision.precision(1).value, equalTo(1));
        assertThat(Precision.of(Integer.MAX_VALUE).value, equalTo(Integer.MAX_VALUE));
        assertThat(Precision.precision(Integer.MAX_VALUE).value, equalTo(Integer.MAX_VALUE));

        int positiveValue = randomInt(2, Integer.MAX_VALUE - 1);
        assertThat(Precision.of(positiveValue).value, equalTo(positiveValue));
        assertThat(Precision.precision(positiveValue).value, equalTo(positiveValue));
    }

    @Test
    public void shouldFailWhenCreatingZeroDigitPrecision() {
        try {
            Precision.of(0);

            fail("zero precision should fail with IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            Precision.precision(0);

            fail("zero precision should fail with IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void shouldFailWhenCreatingNegativeDigitPrecision() {
        for (int value : newList(-1, Integer.MIN_VALUE, randomInt(Integer.MIN_VALUE + 1, -2))) {
            try {
                Precision.of(value);

                fail("Precision for " + value + " significant digits should fail with IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
                // expected
            }

            try {
                Precision.precision(value);

                fail("Precision for " + value + " significant digits should fail with IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
                // expected
            }
        }
    }

    @Test
    public void shouldHaveWorkingEquals() {
        int value = randomInt(positive(), notZero());

        assertThat(Precision.of(value), equalTo(Precision.precision(value)));

        int otherValue = randomInt(otherThan(value), positive(), notZero());

        assertThat(Precision.of(value), not(equalTo(Precision.precision(otherValue))));
    }

    @Test
    public void shouldHaveWorkingHashCode() {
        int value = randomInt(positive(), notZero());

        assertThat(Precision.of(value).hashCode(), equalTo(Precision.precision(value).hashCode()));
    }

    @Test
    public void shouldProvideHandyConstants() {
        assertThat(Precision._7_SIGNIFICANT_DIGITS.value, equalTo(7));
        assertThat(Precision._16_SIGNIFICANT_DIGITS.value, equalTo(16));
        assertThat(Precision._34_SIGNIFICANT_DIGITS.value, equalTo(34));
    }
}