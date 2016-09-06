package javafixes.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An {@link java.util.concurrent.ScheduledExecutorService ScheduledExecutorService} alternative
 * that allows you to monitor the number of submitted, failed and succeeded task, plus has the
 * ability to wait until all tasks are completed = there are no scheduled or running tasks.
 *
 * @author mtymes
 */
public class Runner extends MonitoringTaskSubmitter {

    /**
     * Constructs a runner with specific number of executor thread.
     *
     * @param threadCount number of executor threads
     */
    public Runner(int threadCount) {
        super(newScheduledThreadPool(threadCount));
    }

    /**
     * Factory method to create a {@code Runner}.
     *
     * @param threadCount number of executor threads
     * @return new {@code Runner}
     */
    public static Runner runner(int threadCount) {
        return new Runner(threadCount);
    }

    /**
     * Sends a shutdown signal to the runner.
     *
     * @return this {@code Runner} instance
     * @see ExecutorService#shutdown()
     */
    public Runner shutdown() {
        executor.shutdown();
        return this;
    }

    /**
     * Sends a shutdownNow signal to the runner.
     *
     * @return this {@code Runner} instance
     * @see ExecutorService#shutdownNow()
     */
    public Runner shutdownNow() {
        executor.shutdownNow();
        return this;
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return this {@code Runner} instance
     * @throws RuntimeException wrapping an {@code InterruptedException} if interrupted while waiting
     */
    public Runner awaitTermination(long timeout, TimeUnit unit) {
        try {
            executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Calls {@link #shutdown()} and {@link #awaitTermination(long, TimeUnit)} methods in sequence
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return this {@code Runner} instance
     * @throws RuntimeException wrapping an {@code InterruptedException} if interrupted while waiting
     */
    public Runner shutdownAndAwaitTermination(long timeout, TimeUnit unit) {
        return shutdown().awaitTermination(timeout, unit);
    }

    /**
     * Calls {@link #shutdown()} and {@link #awaitTermination(long, TimeUnit)} methods in sequence
     * where the timeout is set to 5 seconds.
     *
     * @return this {@code Runner} instance
     * @throws RuntimeException wrapping an {@code InterruptedException} if interrupted while waiting
     */
    public Runner shutdownAndAwaitTermination() {
        return shutdownAndAwaitTermination(5, SECONDS);
    }

    /**
     * Calls {@link #shutdownNow()} and {@link #awaitTermination(long, TimeUnit)} methods in sequence
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return this {@code Runner} instance
     * @throws RuntimeException wrapping an {@code InterruptedException} if interrupted while waiting
     */
    public Runner shutdownNowAndAwaitTermination(long timeout, TimeUnit unit) {
        return shutdownNow().awaitTermination(timeout, unit);
    }

    /**
     * Calls {@link #shutdownNow()} and {@link #awaitTermination(long, TimeUnit)} methods in sequence
     * where the timeout is set to 5 seconds.
     *
     * @return this {@code Runner} instance
     * @throws RuntimeException wrapping an {@code InterruptedException} if interrupted while waiting
     */
    public Runner shutdownNowAndAwaitTermination() {
        return shutdownNowAndAwaitTermination(5, SECONDS);
    }
}
