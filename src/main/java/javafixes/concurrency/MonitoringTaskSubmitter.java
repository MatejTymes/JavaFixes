package javafixes.concurrency;

import javafixes.common.function.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A wrapper around {@link java.util.concurrent.ScheduledExecutorService ScheduledExecutorService}
 * that allows you to monitor the number of submitted, failed and succeeded task, plus has the
 * ability to wait until all tasks scheduled trough {@link MonitoringTaskSubmitter} are completed
 * = there are no scheduled or running tasks.
 *
 * @author mtymes
 * @since 10/22/14 10:41 PM
 */
public class MonitoringTaskSubmitter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicInteger failedToSubmit = new AtomicInteger(0);
    private final AtomicInteger succeeded = new AtomicInteger(0);
    private final AtomicInteger failed = new AtomicInteger(0);

    protected final ReusableCountLatch latch = new ReusableCountLatch();

    protected final ScheduledExecutorService executor;

    /**
     * @param executor executor that will be used to execute tasks
     */
    public MonitoringTaskSubmitter(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Schedules a {@link Callable} for immediate execution.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param callable {@link Callable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link Callable}
     */
    public <T> Future<T> run(Callable<T> callable) {
        return submit(asMonitoredCallable(callable));
    }

    /**
     * Schedules a {@link Task} for immediate execution
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param task {@link Task} to be executed
     * @return {@link Future} referring to the state of submitted {@link Task}
     */
    public Future<Void> run(Task task) {
        return submit(asMonitoredCallable(task));
    }

    /**
     * Schedules a {@link Callable} for delayed execution.
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay    the value of the delay
     * @param unit     the {@link TimeUnit} of the {@code delay} argument
     * @param callable {@link Callable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link Callable}
     */
    public <T> ScheduledFuture<T> runIn(long delay, TimeUnit unit, Callable<T> callable) {
        return schedule(delay, unit, asMonitoredCallable(callable));
    }

    /**
     * Schedules a {@link Task} for delayed execution
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay the value of the delay
     * @param unit  the {@link TimeUnit} of the {@code delay} argument
     * @param task  {@link Task} to be executed
     * @return {@link Future} referring to the state of submitted {@link Task}
     */
    public ScheduledFuture<Void> runIn(long delay, TimeUnit unit, Task task) {
        return schedule(delay, unit, asMonitoredCallable(task));
    }

    /**
     * Schedules a {@link Callable} for immediate execution.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param callable {@link Callable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link Callable}
     */
    public <T> Future<T> runCallable(Callable<T> callable) {
        return submit(asMonitoredCallable(callable));
    }

    /**
     * Schedules a {@link Runnable} for immediate execution
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param runnable {@link Runnable} to be executed
     * @return {@link Future} referring to the state of submitted {@link Runnable}
     */
    public Future<Void> runRunnable(Runnable runnable) {
        return submit(asMonitoredCallable(runnable));
    }

    /**
     * Schedules a {@link Task} for immediate execution
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param task {@link Task} to be executed
     * @return {@link Future} referring to the state of submitted {@link Task}
     */
    public Future<Void> runTask(Task task) {
        return submit(asMonitoredCallable(task));
    }

    /**
     * Schedules a {@link Callable} for delayed execution.
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay    the value of the delay
     * @param unit     the {@link TimeUnit} of the {@code delay} argument
     * @param callable {@link Callable} to be executed
     * @param <T>      type of return value
     * @return {@link Future} referring to the state of submitted {@link Callable}
     */
    public <T> ScheduledFuture<T> runCallableIn(long delay, TimeUnit unit, Callable<T> callable) {
        return schedule(delay, unit, asMonitoredCallable(callable));
    }

    /**
     * Schedules a {@link Runnable} for delayed execution
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay    the value of the delay
     * @param unit     the {@link TimeUnit} of the {@code delay} argument
     * @param runnable {@link Runnable} to be executed
     * @return {@link Future} referring to the state of submitted {@link Runnable}
     */
    public ScheduledFuture<Void> runRunnableIn(long delay, TimeUnit unit, Runnable runnable) {
        return schedule(delay, unit, asMonitoredCallable(runnable));
    }

    /**
     * Schedules a {@link Task} for delayed execution
     * The delay is defined using the {@code delay} and {@code unit} parameters.
     * It might be executed later though if the {@link ScheduledExecutorService} has no available threads
     * or more tasks are queued for execution before this task.
     *
     * @param delay the value of the delay
     * @param unit  the {@link TimeUnit} of the {@code delay} argument
     * @param task  {@link Task} to be executed
     * @return {@link Future} referring to the state of submitted {@link Task}
     */
    public ScheduledFuture<Void> runTaskIn(long delay, TimeUnit unit, Task task) {
        return schedule(delay, unit, asMonitoredCallable(task));
    }

    /**
     * @return number of scheduled + running tasks
     */
    public int toBeCompletedCount() {
        return latch.getCount();
    }

    /**
     * @return number of tasks that were not accepted by the executor - most possibly because it has been shut down
     */
    public int failedSubmissionCount() {
        return failedToSubmit.get();
    }

    /**
     * @return number of tasks that completed without throwing an exception
     */
    public int succeededCount() {
        return succeeded.get();
    }

    /**
     * @return number of tasks that completed with a thrown exception
     */
    public int failedCount() {
        return failed.get();
    }

    /**
     * Resets failedSubmission, succeeded and failed counter.
     * It doesn't reset the toBeFinished counter as it is a derived number from the number of scheduled + running tasks.
     */
    public void resetCounters() {
        failedToSubmit.set(0);
        succeeded.set(0);
        failed.set(0);
    }

    /**
     * Causes the current thread to wait until there are no unfinished tasks,
     * unless the thread is {@linkplain Thread#interrupt interrupted}.
     * <p></p>
     * <p>If the current count of to be finished tasks is zero then this method returns immediately.
     * <p></p>
     * <p>If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of two things happen:
     * <ul>
     * <li>The count reaches zero due to completion of all to be finished tasks or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current thread.
     * </ul>
     * <p></p>
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link RuntimeException} wrapping an {@link InterruptedException} is thrown
     * and the current thread's interrupted status is cleared.
     *
     * @return this {@link MonitoringTaskSubmitter} instance
     * @throws RuntimeException wrapping a {@link InterruptedException} if the current thread
     *                          is interrupted while waiting
     */
    public MonitoringTaskSubmitter waitTillDone() {
        try {
            latch.waitTillZero();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Causes the current thread to wait until there are no unfinished tasks,
     * unless the thread is {@linkplain Thread#interrupt interrupted}.
     * <p></p>
     * <p>If the current count of to be finished tasks is zero then this method returns immediately.
     * <p></p>
     * <p>If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of two things happen:
     * <ul>
     * <li>The count reaches zero due to completion of all to be finished tasks or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current thread.
     * </ul>
     * <p></p>
     * <p>If the count reaches zero then the method returns with the value {@code true}.
     * <p></p>
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link RuntimeException} wrapping an {@link InterruptedException} is thrown
     * and the current thread's interrupted status is cleared.
     * <p></p>
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.  If the time is less than or equal to zero, the method
     * will not wait at all.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if there are no tasks to be finished and {@code false}
     * if the waiting time elapsed before the count reached zero
     * @throws RuntimeException wrapping a {@link InterruptedException} if the current thread
     *                          is interrupted while waiting
     */
    public boolean waitTillDone(long timeout, TimeUnit unit) {
        try {
            return latch.waitTillZero(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onWorkException(Exception e) {
        logger.error("Exception while running an async process", e);
    }

    protected void onWorkThrowable(Throwable t) {
        logger.error("Throwable while running an async process", t);
    }

    /* =========================== */
    /* ---   private methods   --- */
    /* =========================== */

    private <T> Callable<T> asMonitoredCallable(Callable<T> task) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    T result = task.call();
                    taskFinished();
                    return result;
                } catch (Exception e) {
                    taskFailed();
                    onWorkException(e);
                    throw e;
                } catch (Throwable t) {
                    taskFailed();
                    onWorkThrowable(t);
                    throw new RuntimeException(t);
                }
            }
        };
    }

    private Callable<Void> asMonitoredCallable(Task task) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    task.run();
                    taskFinished();
                    return null;
                } catch (Exception e) {
                    taskFailed();
                    onWorkException(e);
                    throw e;
                } catch (Throwable t) {
                    taskFailed();
                    onWorkThrowable(t);
                    throw new RuntimeException(t);
                }
            }
        };
    }

    private Callable<Void> asMonitoredCallable(Runnable task) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    task.run();
                    taskFinished();
                    return null;
                } catch (Exception e) {
                    taskFailed();
                    onWorkException(e);
                    throw e;
                } catch (Throwable t) {
                    taskFailed();
                    onWorkThrowable(t);
                    throw new RuntimeException(t);
                }
            }
        };
    }

    private <T> Future<T> submit(Callable<T> monitoredTask) {
        try {
            taskSubmitted();

            return executor.submit(monitoredTask);

        } catch (RuntimeException e) {
            taskSubmissionFailed();
            onWorkException(e);
            throw e;
        }
    }

    private <T> ScheduledFuture<T> schedule(long delay, TimeUnit unit, Callable<T> monitoredTask) {
        try {
            taskSubmitted();

            return executor.schedule(monitoredTask, delay, unit);

        } catch (RuntimeException e) {
            taskSubmissionFailed();
            onWorkException(e);
            throw e;
        }
    }

    private void taskSubmitted() {
        latch.increment();
    }

    private void taskSubmissionFailed() {
        failedToSubmit.incrementAndGet();
        latch.decrement();
    }

    private void taskFinished() {
        succeeded.incrementAndGet();
        latch.decrement();
    }

    private void taskFailed() {
        failed.incrementAndGet();
        latch.decrement();
    }
}
