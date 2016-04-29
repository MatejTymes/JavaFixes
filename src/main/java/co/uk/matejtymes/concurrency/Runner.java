package co.uk.matejtymes.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * @author mtymes
 * @since 10/22/14 10:41 PM
 */
public class Runner {

    private final Sync sync = new Sync(0);
    private final ScheduledExecutorService executor;

    public Runner(int threadCount) {
        executor = newScheduledThreadPool(threadCount);
    }

    public static Runner runner(int threadCount) {
        return new Runner(threadCount);
    }

    public <T> Future<T> run(Callable<T> task) {
        return submit(asMonitoredCallable(task));
    }

    public Future<Void> run(Runnable task) {
        return submit(asMonitoredCallable(task));
    }

    public Future<Void> run(Task task) {
        return submit(asMonitoredCallable(task));
    }

    public <T> ScheduledFuture<T> runIn(long delay, TimeUnit unit, Callable<T> task) {
        return schedule(delay, unit, asMonitoredCallable(task));
    }

    public ScheduledFuture<Void> runIn(long delay, TimeUnit unit, Runnable task) {
        return schedule(delay, unit, asMonitoredCallable(task));
    }

    public ScheduledFuture<Void> runIn(long delay, TimeUnit unit, Task task) {
        return schedule(delay, unit, asMonitoredCallable(task));
    }

    public void waitTillDone() {
        try {
            sync.acquireSharedInterruptibly(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean waitTillDone(long timeout, TimeUnit unit) {
        try {
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }

    /* =========================== */
    /* ---   private methods   --- */
    /* =========================== */

    private <T> Callable<T> asMonitoredCallable(Callable<T> task) {
        return () -> {
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
        };
    }

    private Callable<Void> asMonitoredCallable(Task task) {
        return () -> {
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
        };
    }

    private Callable<Void> asMonitoredCallable(Runnable task) {
        return () -> {
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
        sync.increment();
    }

    private void taskSubmitFailed() {
        sync.decrement();
    }

    private void taskFinished() {
        sync.decrement();
    }

    private void taskFailed() {
        sync.decrement();
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
