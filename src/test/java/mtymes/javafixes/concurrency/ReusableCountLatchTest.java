package mtymes.javafixes.concurrency;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static mtymes.javafixes.test.Condition.negative;
import static mtymes.javafixes.test.Condition.positive;
import static mtymes.javafixes.test.Random.randomBoolean;
import static mtymes.javafixes.test.Random.randomInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ReusableCountLatchTest {

    @Test
    public void shouldReflectInitialValue() {
        int positiveCount = randomInt(positive());

        assertThat(new ReusableCountLatch(positiveCount).getCount(), equalTo(positiveCount));
        assertThat(new ReusableCountLatch().getCount(), equalTo(0));
    }

    @Test
    public void shouldFailOnNegativeInitialCount() {
        try {
            int negativeCount = randomInt(negative());

            new ReusableCountLatch(negativeCount).getCount();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void shouldCount() {
        ReusableCountLatch latch = new ReusableCountLatch(0);

        int expectedCount = 0;

        for (int iteration = 0; iteration < 10_000; iteration++) {
            if (randomBoolean()) {
                latch.increment();
                expectedCount++;
            } else {
                latch.decrement();
                if (expectedCount > 0) {
                    expectedCount--;
                }
            }

            assertThat(latch.getCount(), equalTo(expectedCount));
        }
    }

    @Test(timeout = 500L)
    public void shouldNotBlockIfZero() throws Exception {
        ReusableCountLatch latch = new ReusableCountLatch(2);
        latch.decrement();
        latch.decrement();

        // When
        long startTime = System.currentTimeMillis();
        latch.waitTillZero();
        long duration = System.currentTimeMillis() - startTime;

        // Then
        assertThat(duration, lessThan(10L));
    }

    @Test(timeout = 500L)
    public void shouldBlockIfNotZero() throws Exception {
        ReusableCountLatch latch = new ReusableCountLatch(2);
        latch.decrement();

        // When
        long startTime = System.currentTimeMillis();
        boolean isZero = latch.waitTillZero(200, MILLISECONDS);
        long duration = System.currentTimeMillis() - startTime;

        // Then
        assertThat(duration, greaterThanOrEqualTo(200L));
        assertThat(isZero, equalTo(false));
    }

    @Test(timeout = 5_000L)
    public void shouldBlockTillZero() {
        // todo: implement
    }

    @Test
    public void shouldBeReusable() {
        // todo: implement
    }

}