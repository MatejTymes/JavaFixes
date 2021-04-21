package javafixes.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An {@link ScheduledExecutorService} alternative
 * that allows you to monitor the number of submitted, failed and succeeded task, plus has the
 * ability to wait until all tasks are completed = there are no scheduled or running tasks.
 *
 * @author mtymes
 */
public class Runner extends MonitoringTaskSubmitter implements ShutdownInfo, AutoCloseable {

    private final AtomicBoolean wasShutdownTriggered = new AtomicBoolean(false);
    private final AtomicInteger failedToStart = new AtomicInteger(0);

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
     * Schedules a {@link ShutdownAwareCallable} for immediate execution.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param callable {@link ShutdownAwareCallable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareCallable}
     */
    public <T> Future<T> run(ShutdownAwareCallable<T> callable) {
        return runCallable(() -> callable.call(this));
    }

    /**
     * Schedules a {@link ShutdownAwareTask} for immediate execution
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param task {@link ShutdownAwareTask} to be executed
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareTask}
     */
    public Future<Void> run(ShutdownAwareTask task) {
        return runTask(() -> task.run(this));
    }

    /**
     * Schedules a {@link ShutdownAwareCallable} for delayed execution.
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay    the value of the delay
     * @param unit     the {@link TimeUnit} of the {@code delay} argument
     * @param callable {@link ShutdownAwareCallable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareCallable}
     */
    public <T> ScheduledFuture<T> runIn(long delay, TimeUnit unit, ShutdownAwareCallable<T> callable) {
        return runCallableIn(delay, unit, () -> callable.call(this));
    }

    /**
     * Schedules a {@link ShutdownAwareTask} for delayed execution
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay the value of the delay
     * @param unit  the {@link TimeUnit} of the {@code delay} argument
     * @param task  {@link ShutdownAwareTask} to be executed
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareTask}
     */
    public ScheduledFuture<Void> runIn(long delay, TimeUnit unit, ShutdownAwareTask task) {
        return runTaskIn(delay, unit, () -> task.run(this));
    }

    /**
     * Schedules a {@link ShutdownAwareCallable} for immediate execution.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param callable {@link ShutdownAwareCallable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareCallable}
     */
    public <T> Future<T> runCallable(ShutdownAwareCallable<T> callable) {
        return runCallable(() -> callable.call(this));
    }

    /**
     * Schedules a {@link ShutdownAwareTask} for immediate execution
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param task {@link ShutdownAwareTask} to be executed
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareTask}
     */
    public Future<Void> runTask(ShutdownAwareTask task) {
        return runTask(() -> task.run(this));
    }

    /**
     * Schedules a {@link ShutdownAwareCallable} for delayed execution.
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay    the value of the delay
     * @param unit     the {@link TimeUnit} of the {@code delay} argument
     * @param callable {@link ShutdownAwareCallable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareCallable}
     */
    public <T> ScheduledFuture<T> runCallableIn(long delay, TimeUnit unit, ShutdownAwareCallable<T> callable) {
        return runCallableIn(delay, unit, () -> callable.call(this));
    }

    /**
     * Schedules a {@link ShutdownAwareTask} for delayed execution
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay the value of the delay
     * @param unit  the {@link TimeUnit} of the {@code delay} argument
     * @param task  {@link ShutdownAwareTask} to be executed
     * @return {@link Future} referring to the state of submitted {@link ShutdownAwareTask}
     */
    public ScheduledFuture<Void> runTaskIn(long delay, TimeUnit unit, ShutdownAwareTask task) {
        return runTaskIn(delay, unit, () -> task.run(this));
    }

    @Override
    public Runner waitTillDone() {
        super.waitTillDone();
        return this;
    }

    /**
     * Sends a shutdown signal to the runner.
     *
     * @return this {@code Runner} instance
     * @see ExecutorService#shutdown()
     */
    public Runner shutdown() {
        wasShutdownTriggered.set(true);
        executor.shutdown();
        return this;
    }

    /**
     * Sends a shutdownNow signal to the runner.
     *
     * @return this {@code Runner} instance
     * @see ExecutorService#shutdownNow()
     */
    // todo: fix specific case where an task is removed without ever started (and because of this doesn't goes into numberOfDrainedTasks)
    public Runner shutdownNow() {
        wasShutdownTriggered.set(true);
        int numberOfDrainedTasks = executor.shutdownNow().size();
        tasksDrainedFromExecutor(numberOfDrainedTasks);
        return this;
    }

    /**
     * Sends a shutdownNow signal to the runner.
     *
     * @see ExecutorService#shutdownNow()
     */
    @Override
    public void close() throws Exception {
        shutdownNow();
    }

    /**
     * Returns {@code true} if any shutdown operation has been called on this {@code Runner}.
     * In this case the {@code Runner} won't accept any new tasks for execution.
     *
     * @return {@code true} if shutdown on the {@code Runner} has been triggered
     */
    @Override
    public boolean wasShutdownTriggered() {
        return wasShutdownTriggered.get();
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

    /**
     * @return number of tasks that were initially accepted by the executor but removed before they would start - most possibly because the executor has been shut down
     */
    public int failedToStartCount() {
        return failedToStart.get();
    }

    /**
     * Resets failedSubmission, failedToStart, succeeded and failed counter.
     * It doesn't reset the toBeFinished counter as it is a derived number from the number of scheduled + running tasks.
     */
    @Override
    public void resetCounters() {
        failedToStart.set(0);
        super.resetCounters();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.shutdownNow();
        } finally {
            super.finalize();
        }
    }

    private void tasksDrainedFromExecutor(int numberOfTasks) {
        for (int i = 0; i < numberOfTasks; i++) {
            failedToStart.incrementAndGet();
            latch.decrement();
        }
    }
}
