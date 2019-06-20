package javafixes.concurrency;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

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

    private void doSomethingThatTakesTime() {
        long count = 0;
        for (int j = 0; j < 1_000_000; j++) {
            count += j;
        }
    }
}