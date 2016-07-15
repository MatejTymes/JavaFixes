package mtymes.javafixes.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author mtymes
 * @since 10/22/14 10:41 PM
 */
// todo: add success and failure counter
public class MonitoringTaskSubmitter {

    private final ReusableCountLatch latch = new ReusableCountLatch();

    protected final ScheduledExecutorService executor;

    public MonitoringTaskSubmitter(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    public <T> Future<T> run(Callable<T> callable) {
        return submit(asMonitoredCallable(callable));
    }

    public Future<Void> run(Runnable runnable) {
        return submit(asMonitoredCallable(runnable));
    }

    public Future<Void> run(Task task) {
        return submit(asMonitoredCallable(task));
    }

    public <T> ScheduledFuture<T> runIn(long delay, TimeUnit unit, Callable<T> callable) {
        return schedule(delay, unit, asMonitoredCallable(callable));
    }

    public ScheduledFuture<Void> runIn(long delay, TimeUnit unit, Runnable runnable) {
        return schedule(delay, unit, asMonitoredCallable(runnable));
    }

    public ScheduledFuture<Void> runIn(long delay, TimeUnit unit, Task task) {
        return schedule(delay, unit, asMonitoredCallable(task));
    }

    public <T> Future<T> runCallable(Callable<T> callable) {
        return run(callable);
    }

    public Future<Void> runRunnable(Runnable runnable) {
        return run(runnable);
    }

    public Future<Void> runTask(Task task) {
        return run(task);
    }

    public <T> ScheduledFuture<T> runCallableIn(long delay, TimeUnit unit, Callable<T> callable) {
        return runIn(delay, unit, callable);
    }

    public ScheduledFuture<Void> runRunnableIn(long delay, TimeUnit unit, Runnable runnable) {
        return runIn(delay, unit, runnable);
    }

    public ScheduledFuture<Void> runTaskIn(long delay, TimeUnit unit, Task task) {
        return runIn(delay, unit, task);
    }

    public void waitTillDone() {
        try {
            latch.waitTillZero();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean waitTillDone(long timeout, TimeUnit unit) {
        try {
            return latch.waitTillZero(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                    throw e;
                } catch (Throwable t) {
                    taskFailed();
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
                    throw e;
                } catch (Throwable t) {
                    taskFailed();
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
                    throw e;
                } catch (Throwable t) {
                    taskFailed();
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
            taskSubmitFailed();
            throw e;
        }
    }

    private <T> ScheduledFuture<T> schedule(long delay, TimeUnit unit, Callable<T> monitoredTask) {
        try {
            taskSubmitted();

            return executor.schedule(monitoredTask, delay, unit);

        } catch (RuntimeException e) {
            taskSubmitFailed();
            throw e;
        }
    }

    private void taskSubmitted() {
        latch.increment();
    }

    private void taskSubmitFailed() {
        latch.decrement();
    }

    private void taskFinished() {
        latch.decrement();
    }

    private void taskFailed() {
        latch.decrement();
    }

    // class based on CountDownLatch.Sync
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        protected void increment() {
            for (; ; ) {
                int oldCount = getState();
                int newCount = oldCount + 1;
                if (compareAndSetState(oldCount, newCount)) {
                    return;
                }
            }
        }

        protected void decrement() {
            releaseShared(1);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (; ; ) {
                int oldCount = getState();
                if (oldCount == 0) {
                    return false;
                }
                int newCount = oldCount - 1;
                if (compareAndSetState(oldCount, newCount)) {
                    return newCount == 0;
                }
            }
        }
    }
}
