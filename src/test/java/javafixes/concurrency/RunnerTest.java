package javafixes.concurrency;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.test.Random.randomLong;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RunnerTest extends BaseExecutorTest {

    protected Runner executor;

    @Override
    protected void initializeExecutor() {
        // create executor with 10 threads
        executor = Runner.runner(10);
    }

    @Override
    protected void shutdownExecutor() throws InterruptedException {
        executor.shutdownAndAwaitTermination();
    }

    @Override
    protected MonitoringTaskSubmitter executor() {
        return executor;
    }

    @Test
    public void shouldNotDeadlockOnShutdownNow() {
        // fill the queue
        for (int i = 0; i < 100; i++) {
            executor.run(this::doSomethingThatTakesTime);
        }

        // When
        executor.shutdownNow();

        // Then
        boolean done = executor.waitTillDone(300, TimeUnit.MILLISECONDS);
        assertThat(done, is(true));
        assertThat(executor.toBeCompletedCount(), is(0));
        assertThat(executor.failedToStartCount(), greaterThan(0));
    }

    @Test
    public void shouldInformShutdownAwareTasksThatShutdownHasBeenTriggered() throws InterruptedException {
        List<Consumer<Runner>> shutdownMethods = newList(
                Runner::shutdown,
                Runner::shutdownNow
        );
        List<ShutdownAwareRunCheck> shutdownAwareRunChecks = newList(
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.run(shutdownInfo -> { //

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();

                    return 0;
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.run(shutdownInfo -> {

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.runIn(randomLong(100, 300), TimeUnit.MILLISECONDS, shutdownInfo -> { //

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();

                    return 0;
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.runIn(randomLong(100, 300), TimeUnit.MILLISECONDS, shutdownInfo -> {

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.runCallable(shutdownInfo -> { //

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();

                    return 0;
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.runTask(shutdownInfo -> {

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.runCallableIn(randomLong(100, 300), TimeUnit.MILLISECONDS, shutdownInfo -> { //

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();

                    return 0;
                }),
                (runner, startCheck, endCheck, wasShutdownInfoReceived) -> runner.runTaskIn(randomLong(100, 300), TimeUnit.MILLISECONDS, shutdownInfo -> {

                    startCheck.countDown();

                    while (!shutdownInfo.wasShutdownTriggered()) {
                        doSomethingThatTakesTime();
                    }
                    wasShutdownInfoReceived.set(shutdownInfo.wasShutdownTriggered());

                    endCheck.countDown();
                })
        );

        for (Consumer<Runner> shutdownMethod : shutdownMethods) {
            for (ShutdownAwareRunCheck shutdownAwareRunCheck : shutdownAwareRunChecks) {
                AtomicBoolean wasShutdownInfoReceived = new AtomicBoolean(false);
                CountDownLatch startCheck = new CountDownLatch(1);
                CountDownLatch endCheck = new CountDownLatch(1);
                Runner runner = Runner.runner(1);

                shutdownAwareRunCheck.runCheck(runner, startCheck, endCheck, wasShutdownInfoReceived);
                startCheck.await();

                shutdownMethod.accept(runner);
                assertThat(runner.wasShutdownTriggered(), is(true));

                endCheck.await(1, TimeUnit.SECONDS);

                boolean done = executor.waitTillDone(1, TimeUnit.SECONDS);

                assertThat(done, is(true));
                assertThat(wasShutdownInfoReceived.get(), is(true));
            }
        }
    }


    private interface ShutdownAwareRunCheck {

        void runCheck(Runner runner, CountDownLatch startCheck, CountDownLatch endCheck, AtomicBoolean wasShutdownInfoReceived);
    }

    private void doSomethingThatTakesTime() {
        long count = 0;
        for (int j = 0; j < 1_000_000; j++) {
            count += j;
        }
    }
}