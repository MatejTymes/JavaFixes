package javafixes.concurrency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public abstract class BaseExecutorTest {

    protected abstract void initializeExecutor();

    protected abstract void shutdownExecutor() throws InterruptedException;

    protected abstract MonitoringTaskSubmitter executor();

    @Before
    public void setUp() throws Exception {
        initializeExecutor();
    }

    @After
    public void tearDown() throws Exception {
        shutdownExecutor();
    }

    @Test
    public void shouldCountNumberOfFailedTasks() {

        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().runRunnable(() -> {
            throw new RuntimeException();
        });
        executor().runRunnableIn(50, TimeUnit.MILLISECONDS, () -> {
            throw new IllegalArgumentException();
        });
        executor().runCallable(() -> {
            throw new RuntimeException();
        });
        executor().runCallableIn(50, TimeUnit.MILLISECONDS, () -> {
            throw new IllegalArgumentException();
        });
        executor().runTask(() -> {
            throw new RuntimeException();
        });
        executor().runTaskIn(50, TimeUnit.MILLISECONDS, () -> {
            throw new IllegalArgumentException();
        });

        executor().waitTillDone();

        assertThat(executor().failedCount(), equalTo(6));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().resetCounters();

        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));
    }

    @Test
    public void shouldCountNumberOfSucceededTasks() {

        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().runRunnable(() -> {
        });
        executor().runRunnableIn(50, TimeUnit.MILLISECONDS, () -> {
        });
        executor().runCallable(() -> null);
        executor().runCallableIn(50, TimeUnit.MILLISECONDS, () -> null);
        executor().runTask(() -> {
        });
        executor().runTaskIn(50, TimeUnit.MILLISECONDS, () -> {
        });

        executor().waitTillDone();

        assertThat(executor().succeededCount(), equalTo(6));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().resetCounters();

        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));
    }

    @Test
    public void shouldCountNumberOfToBeCompletedTasks() {

        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().runRunnable(() -> sleepForMs(500));
        executor().runRunnableIn(500, TimeUnit.MILLISECONDS, () -> {
        });
        executor().runCallable(() -> {
            sleepForMs(500);
            return true;
        });
        executor().runCallableIn(500, TimeUnit.MILLISECONDS, () -> null);
        executor().runTask(() -> sleepForMs(500));
        executor().runTaskIn(500, TimeUnit.MILLISECONDS, () -> {
        });

        assertThat(executor().toBeCompletedCount(), equalTo(6));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().resetCounters();

        assertThat(executor().toBeCompletedCount(), equalTo(6));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().waitTillDone();

        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(6));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));

        executor().resetCounters();

        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
        assertThat(executor().failedSubmissionCount(), equalTo(0));
    }

    @Test
    public void shouldCountNumberOfFailedSubmissionCount() throws Exception {

        shutdownExecutor();

        assertThat(executor().failedSubmissionCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));

        try {
            executor().runRunnable(() -> sleepForMs(500));
            fail("expected RejectedExecutionException");
        } catch (RejectedExecutionException expected) {
            // do nothing
        }
        try {
            executor().runRunnableIn(500, TimeUnit.MILLISECONDS, () -> {
            });
            fail("expected RejectedExecutionException");
        } catch (RejectedExecutionException expected) {
            // do nothing
        }
        try {
            executor().runCallable(() -> {
                sleepForMs(500);
                return true;
            });
            fail("expected RejectedExecutionException");
        } catch (RejectedExecutionException expected) {
            // do nothing
        }
        try {
            executor().runCallableIn(500, TimeUnit.MILLISECONDS, () -> null);
            fail("expected RejectedExecutionException");
        } catch (RejectedExecutionException expected) {
            // do nothing
        }
        try {
            executor().runTask(() -> sleepForMs(500));
            fail("expected RejectedExecutionException");
        } catch (RejectedExecutionException expected) {
            // do nothing
        }
        try {
            executor().runTaskIn(500, TimeUnit.MILLISECONDS, () -> {
            });
            fail("expected RejectedExecutionException");
        } catch (RejectedExecutionException expected) {
            // do nothing
        }

        assertThat(executor().failedSubmissionCount(), equalTo(6));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));

        executor().resetCounters();

        assertThat(executor().failedSubmissionCount(), equalTo(0));
        assertThat(executor().toBeCompletedCount(), equalTo(0));
        assertThat(executor().succeededCount(), equalTo(0));
        assertThat(executor().failedCount(), equalTo(0));
    }

    private void sleepForMs(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
