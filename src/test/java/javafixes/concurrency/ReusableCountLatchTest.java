package javafixes.concurrency;

import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javafixes.test.Condition.negative;
import static javafixes.test.Condition.positive;
import static javafixes.test.Random.randomBoolean;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.*;
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
        assertThat(isZero, is(false));
    }

    @Test(timeout = 2_000L)
    public void shouldBlockTillZero() throws Exception {
        ScheduledExecutorService executor = newScheduledThreadPool(2);
        try {
            ReusableCountLatch latch = new ReusableCountLatch();

            latch.increment();
            latch.increment();

            executor.schedule(latch::decrement, 200L, MILLISECONDS);
            executor.schedule(latch::decrement, 600L, MILLISECONDS);

            long startTime = System.currentTimeMillis();
            latch.waitTillZero();
            long duration = System.currentTimeMillis() - startTime;

            assertThat(duration, greaterThanOrEqualTo(550L));

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldBeReusable() throws Exception {
        ReusableCountLatch latch = new ReusableCountLatch();
        latch.increment();

        long startTime = System.currentTimeMillis();
        boolean isZero = latch.waitTillZero(150, MILLISECONDS);
        long duration = System.currentTimeMillis() - startTime;
        assertThat(isZero, is(false));
        assertThat(duration, greaterThanOrEqualTo(150L));

        latch.decrement();

        startTime = System.currentTimeMillis();
        isZero = latch.waitTillZero(150, MILLISECONDS);
        duration = System.currentTimeMillis() - startTime;
        assertThat(isZero, is(true));
        assertThat(duration, lessThan(10L));

        latch.increment();
        latch.increment();

        startTime = System.currentTimeMillis();
        isZero = latch.waitTillZero(150, MILLISECONDS);
        duration = System.currentTimeMillis() - startTime;
        assertThat(isZero, is(false));
        assertThat(duration, greaterThanOrEqualTo(150L));

        latch.decrement();
        latch.decrement();

        startTime = System.currentTimeMillis();
        isZero = latch.waitTillZero(150, MILLISECONDS);
        duration = System.currentTimeMillis() - startTime;
        assertThat(isZero, is(true));
        assertThat(duration, lessThan(10L));
    }

}