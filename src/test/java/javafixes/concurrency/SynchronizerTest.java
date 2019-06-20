package javafixes.concurrency;

import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.UUID.randomUUID;
import static javafixes.common.CollectionUtil.newList;
import static javafixes.concurrency.Runner.runner;
import static javafixes.test.Random.randomInt;
import static javafixes.test.Random.randomUUIDString;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SynchronizerTest {

    private Synchronizer<String> synchronizer = new Synchronizer<>();

    @Test
    public void shouldExecuteCallable() {
        AtomicInteger numberOfCalls = new AtomicInteger(0);
        UUID expectedResult = randomUUID();

        UUID actalResult = synchronizer.synchronizeOn(
                randomUUIDString(),
                (Callable<UUID>) () -> {
                    numberOfCalls.incrementAndGet();

                    return expectedResult;
                }
        );

        assertThat(numberOfCalls.get(), equalTo(1));
        assertThat(actalResult, equalTo(expectedResult));
    }

    @Test
    public void shouldExecuteRunnable() {
        AtomicInteger numberOfCalls = new AtomicInteger(0);

        synchronizer.synchronizeRunnableOn(
                randomUUIDString(),
                (Runnable) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );

        assertThat(numberOfCalls.get(), equalTo(1));
    }

    @Test
    public void shouldExecuteTask() {
        AtomicInteger numberOfCalls = new AtomicInteger(0);

        synchronizer.synchronizeOn(
                randomUUIDString(),
                (Task) () -> {
                    numberOfCalls.incrementAndGet();
                }
        );

        assertThat(numberOfCalls.get(), equalTo(1));
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
}