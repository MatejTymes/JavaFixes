package javafixes.concurrency;

import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.UUID.randomUUID;
import static javafixes.common.CollectionUtil.newList;
import static javafixes.concurrency.Runner.runner;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SynchronizerTest {

    private Synchronizer<String> synchronizer = new Synchronizer<>();

    @Test
    public void shouldExecuteAction() throws Exception {
        AtomicInteger numberOfCalls = new AtomicInteger(0);
        UUID expectedResult = randomUUID();
        UUID actalResult;


        numberOfCalls.set(0);
        actalResult = synchronizer.synchronizeOn(
                randomUUIDString(),
                (Callable<UUID>) () -> {
                    numberOfCalls.incrementAndGet();

                    return expectedResult;
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));
        assertThat(actalResult, equalTo(expectedResult));


        numberOfCalls.set(0);
        actalResult = synchronizer.synchronizeOn(
                randomUUIDString(),
                randomLong(1, 9_999),
                pickRandomValue(TimeUnit.MILLISECONDS, TimeUnit.SECONDS),
                (Callable<UUID>) () -> {
                    numberOfCalls.incrementAndGet();

                    return expectedResult;
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));
        assertThat(actalResult, equalTo(expectedResult));


        numberOfCalls.set(0);
        synchronizer.synchronizeRunnableOn(
                randomUUIDString(),
                (Runnable) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));


        numberOfCalls.set(0);
        synchronizer.synchronizeRunnableOn(
                randomUUIDString(),
                randomLong(1, 9_999),
                pickRandomValue(TimeUnit.MILLISECONDS, TimeUnit.SECONDS),
                (Runnable) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));


        numberOfCalls.set(0);
        synchronizer.synchronizeOn(
                randomUUIDString(),
                (Task) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));


        numberOfCalls.set(0);
        synchronizer.synchronizeOn(
                randomUUIDString(),
                randomLong(1, 9_999),
                pickRandomValue(TimeUnit.MILLISECONDS, TimeUnit.SECONDS),
                (Task) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));
    }


    @Test
    public void shouldWrapExceptionOnFailure() throws Exception {
        AtomicInteger numberOfCalls = new AtomicInteger();
        Exception expectedException = randomException();
        RuntimeException expectedRuntimeException = randomRuntimeException();


        numberOfCalls.set(0);
        try {
            synchronizer.synchronizeOn(
                    randomUUIDString(),
                    (Callable<UUID>) () -> {
                        numberOfCalls.incrementAndGet();

                        throw expectedException;
                    }
            );

            fail("Expected WrappedException");

        } catch (WrappedException wrappedException) {
            assertThat(wrappedException.getCause(), equalTo(expectedException));
            assertThat(numberOfCalls.get(), equalTo(1));
        }


        numberOfCalls.set(0);
        try {
            synchronizer.synchronizeOn(
                    randomUUIDString(),
                    randomLong(1, 9_999),
                    pickRandomValue(TimeUnit.MILLISECONDS, TimeUnit.SECONDS),
                    (Callable<UUID>) () -> {
                        numberOfCalls.incrementAndGet();

                        throw expectedException;
                    }
            );

            fail("Expected WrappedException");

        } catch (WrappedException wrappedException) {
            assertThat(wrappedException.getCause(), equalTo(expectedException));
            assertThat(numberOfCalls.get(), equalTo(1));
        }


        numberOfCalls.set(0);
        try {
            synchronizer.synchronizeRunnableOn(
                    randomUUIDString(),
                    (Runnable) () -> {
                        numberOfCalls.incrementAndGet();

                        throw expectedRuntimeException;
                    }
            );

            fail("Expected WrappedException");

        } catch (WrappedException wrappedException) {
            assertThat(wrappedException.getCause(), equalTo(expectedRuntimeException));
            assertThat(numberOfCalls.get(), equalTo(1));
        }


        numberOfCalls.set(0);
        try {
            synchronizer.synchronizeRunnableOn(
                    randomUUIDString(),
                    randomLong(1, 9_999),
                    pickRandomValue(TimeUnit.MILLISECONDS, TimeUnit.SECONDS),
                    (Runnable) () -> {
                        numberOfCalls.incrementAndGet();

                        throw expectedRuntimeException;
                    }
            );

            fail("Expected WrappedException");

        } catch (WrappedException wrappedException) {
            assertThat(wrappedException.getCause(), equalTo(expectedRuntimeException));
            assertThat(numberOfCalls.get(), equalTo(1));
        }


        numberOfCalls.set(0);
        try {
            synchronizer.synchronizeOn(
                    randomUUIDString(),
                    (Task) () -> {
                        numberOfCalls.incrementAndGet();

                        throw expectedException;
                    }
            );

            fail("Expected WrappedException");

        } catch (WrappedException wrappedException) {
            assertThat(wrappedException.getCause(), equalTo(expectedException));
            assertThat(numberOfCalls.get(), equalTo(1));
        }


        numberOfCalls.set(0);
        try {
            synchronizer.synchronizeOn(
                    randomUUIDString(),
                    randomLong(1, 9_999),
                    pickRandomValue(TimeUnit.MILLISECONDS, TimeUnit.SECONDS),
                    (Task) () -> {
                        numberOfCalls.incrementAndGet();

                        throw expectedException;
                    }
            );

            fail("Expected WrappedException");

        } catch (WrappedException wrappedException) {
            assertThat(wrappedException.getCause(), equalTo(expectedException));
            assertThat(numberOfCalls.get(), equalTo(1));
        }
    }

    @Test
    public void shouldNotFailIfWaitingForExecutionIsWithinTimeLimit() throws Exception {
        AtomicInteger numberOfCalls = new AtomicInteger(0);

        String key;
        UUID expectedResult = randomUUID();
        UUID actalResult;

        long blockDurationInMs = randomLong(750, 2_500);
        long waitTimeInMs = blockDurationInMs + randomLong(2_000, 5_000);

        key = randomUUIDString();
        numberOfCalls.set(0);
        blockKeyFor(key, blockDurationInMs, TimeUnit.MILLISECONDS);
        actalResult = synchronizer.synchronizeOn(
                key,
                waitTimeInMs,
                TimeUnit.MILLISECONDS,
                (Callable<UUID>) () -> {
                    numberOfCalls.incrementAndGet();

                    return expectedResult;
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));
        assertThat(actalResult, equalTo(expectedResult));


        key = randomUUIDString();
        numberOfCalls.set(0);
        blockKeyFor(key, blockDurationInMs, TimeUnit.MILLISECONDS);
        synchronizer.synchronizeRunnableOn(
                key,
                waitTimeInMs,
                TimeUnit.MILLISECONDS,
                (Runnable) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));


        key = randomUUIDString();
        numberOfCalls.set(0);
        blockKeyFor(key, blockDurationInMs, TimeUnit.MILLISECONDS);
        synchronizer.synchronizeOn(
                key,
                waitTimeInMs,
                TimeUnit.MILLISECONDS,
                (Task) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );
        assertThat(numberOfCalls.get(), equalTo(1));
    }

    @Test
    public void shouldFailIfWaitingForExecutionIsOutsideTimeLimit() throws Exception {
        AtomicInteger numberOfCalls = new AtomicInteger(0);

        String key;

        long waitTimeInMs = randomLong(750, 2_500);
        long blockDurationInMs = waitTimeInMs + randomLong(2_000, 5_000);
        long startTime;


        key = randomUUIDString();
        numberOfCalls.set(0);
        blockKeyFor(key, blockDurationInMs, TimeUnit.MILLISECONDS);
        startTime = System.currentTimeMillis();
        try {
            synchronizer.synchronizeOn(
                    key,
                    waitTimeInMs,
                    TimeUnit.MILLISECONDS,
                    (Callable<UUID>) () -> {
                        numberOfCalls.incrementAndGet();

                        return randomUUID();
                    }
            );

            fail("expected TimeoutException");

        } catch (TimeoutException expectedException) {
            long duration = System.currentTimeMillis() - startTime;
            assertThat(numberOfCalls.get(), equalTo(0));
            assertThat(duration, greaterThanOrEqualTo(waitTimeInMs));
            assertThat(duration, lessThan(waitTimeInMs + 1_000));
        }


        key = randomUUIDString();
        numberOfCalls.set(0);
        blockKeyFor(key, blockDurationInMs, TimeUnit.MILLISECONDS);
        startTime = System.currentTimeMillis();
        try {
            synchronizer.synchronizeRunnableOn(
                    key,
                    waitTimeInMs,
                    TimeUnit.MILLISECONDS,
                    (Runnable) () -> {
                        numberOfCalls.incrementAndGet();
                    }
            );

            fail("expected TimeoutException");

        } catch (TimeoutException expectedException) {
            long duration = System.currentTimeMillis() - startTime;
            assertThat(numberOfCalls.get(), equalTo(0));
            assertThat(duration, greaterThanOrEqualTo(waitTimeInMs));
            assertThat(duration, lessThan(waitTimeInMs + 1_000));
        }


        key = randomUUIDString();
        numberOfCalls.set(0);
        blockKeyFor(key, blockDurationInMs, TimeUnit.MILLISECONDS);
        startTime = System.currentTimeMillis();
        try {
            synchronizer.synchronizeOn(
                    key,
                    waitTimeInMs,
                    TimeUnit.MILLISECONDS,
                    (Task) () -> {
                        numberOfCalls.incrementAndGet();
                    }
            );

            fail("expected TimeoutException");

        } catch (TimeoutException expectedException) {
            long duration = System.currentTimeMillis() - startTime;
            assertThat(numberOfCalls.get(), equalTo(0));
            assertThat(duration, greaterThanOrEqualTo(waitTimeInMs));
            assertThat(duration, lessThan(waitTimeInMs + 1_000));
        }
    }


    @Test
    public void shouldRunOnlyOneTaskForASpecificIdAtATime() {
        int numberOfThreads = 3;
        int numberOfRetries = 20;

        Runner runner = runner(numberOfThreads);
        try {
            for (int retryAttempt = 1; retryAttempt <= numberOfRetries; retryAttempt++) {

                AtomicInteger maxThreadRunningAtATime = new AtomicInteger(0);

                AtomicInteger countOfThreadsCurrentlyRunning = new AtomicInteger(0);
                for (int threadIndex = 0; threadIndex < numberOfThreads; threadIndex++) {
                    runner.runTask(() -> {
                        Thread.sleep(randomInt(1, 10));

                        synchronizer.synchronizeOn("id" + 123, () -> {
                            int runningThreads = countOfThreadsCurrentlyRunning.incrementAndGet();
                            if (runningThreads > maxThreadRunningAtATime.get()) {
                                maxThreadRunningAtATime.set(runningThreads);

                            }
                            Thread.sleep(randomInt(1, 10));
                            countOfThreadsCurrentlyRunning.decrementAndGet();
                        });
                    });
                }
                runner.waitTillDone();

                assertThat(maxThreadRunningAtATime.get(), is(1));
            }
        } finally {
            runner.shutdownNow();
        }
    }

    @Test
    public void shouldRunOnlyOneRunnableForASpecificIdAtATime() {
        int numberOfThreads = 3;
        int numberOfRetries = 20;

        Runner runner = runner(numberOfThreads);
        try {
            for (int retryAttempt = 1; retryAttempt <= numberOfRetries; retryAttempt++) {

                AtomicInteger maxThreadRunningAtATime = new AtomicInteger(0);

                AtomicInteger countOfThreadsCurrentlyRunning = new AtomicInteger(0);
                for (int threadIndex = 0; threadIndex < numberOfThreads; threadIndex++) {
                    runner.runTask(() -> {
                        Thread.sleep(randomInt(1, 10));

                        synchronizer.synchronizeRunnableOn("id" + 123, () -> {
                            int runningThreads = countOfThreadsCurrentlyRunning.incrementAndGet();
                            if (runningThreads > maxThreadRunningAtATime.get()) {
                                maxThreadRunningAtATime.set(runningThreads);

                            }
                            try {
                                Thread.sleep(randomInt(1, 10));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            countOfThreadsCurrentlyRunning.decrementAndGet();
                        });
                    });
                }
                runner.waitTillDone();

                assertThat(maxThreadRunningAtATime.get(), is(1));
            }
        } finally {
            runner.shutdownNow();
        }
    }

    @Test
    public void shouldRunOnlyOneCallableForASpecificIdAtATime() {
        int numberOfThreads = 3;
        int numberOfRetries = 20;

        Runner runner = runner(numberOfThreads);
        try {
            for (int retryAttempt = 1; retryAttempt <= numberOfRetries; retryAttempt++) {

                AtomicInteger maxThreadRunningAtATime = new AtomicInteger(0);

                AtomicInteger countOfThreadsCurrentlyRunning = new AtomicInteger(0);
                for (int threadIndex = 0; threadIndex < numberOfThreads; threadIndex++) {
                    runner.runTask(() -> {
                        Thread.sleep(randomInt(1, 10));

                        synchronizer.synchronizeOn("id" + 123, () -> {
                            int runningThreads = countOfThreadsCurrentlyRunning.incrementAndGet();
                            if (runningThreads > maxThreadRunningAtATime.get()) {
                                maxThreadRunningAtATime.set(runningThreads);

                            }
                            Thread.sleep(randomInt(1, 10));
                            return countOfThreadsCurrentlyRunning.decrementAndGet();
                        });
                    });
                }
                runner.waitTillDone();

                assertThat(maxThreadRunningAtATime.get(), is(1));
            }
        } finally {
            runner.shutdownNow();
        }
    }

    @Test
    public void shouldRunActionsForDifferentIdsInParallel() {
        int numberOfIds = 100;
        Runner runner = runner(numberOfIds * 3);
        try {
            long startTime = System.currentTimeMillis();
            List<AtomicInteger> maxActionsRunPerIdCounters = newList();
            for (int i = 0; i < numberOfIds; i++) {
                AtomicInteger maxActionsRunPerIdCounter = new AtomicInteger(0);
                AtomicInteger currentlyRunningActionsCounter = new AtomicInteger(0);
                maxActionsRunPerIdCounters.add(maxActionsRunPerIdCounter);
                for (int j = 0; j < 3; j++) {
                    String id = "id" + i;
                    runner.runTask(() -> {
                        synchronizer.synchronizeOn(id, () -> {
                            int count = currentlyRunningActionsCounter.incrementAndGet();
                            if (maxActionsRunPerIdCounter.get() < count) {
                                maxActionsRunPerIdCounter.set(count);
                            }
                            Thread.sleep(500);
                            currentlyRunningActionsCounter.decrementAndGet();
                        });
                    });
                }
            }
            runner.waitTillDone();
            long duration = System.currentTimeMillis() - startTime;

            assertThat(duration, lessThan(2_000L));
            for (AtomicInteger maxActionsRunPerIdCounter : maxActionsRunPerIdCounters) {
                assertThat(maxActionsRunPerIdCounter.get(), equalTo(1));
            }
        } finally {
            runner.shutdownNow();
        }
    }

    private void blockKeyFor(String key, long blockDuration, TimeUnit timeUnit) throws InterruptedException {
        Runner runner = runner(1);
        CountDownLatch hasStarted = new CountDownLatch(1);
        runner.run(() -> {
            synchronizer.synchronizeOn(key, () -> {
                try {
                    hasStarted.countDown();
                    Thread.sleep(timeUnit.toMillis(blockDuration));
                } finally {
                    runner.shutdown();
                }
            });
        });
        hasStarted.await();
    }
}