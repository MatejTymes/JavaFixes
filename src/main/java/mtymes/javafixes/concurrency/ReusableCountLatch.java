package mtymes.javafixes.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author mtymes
 * @since 07/10/15 00:10 AM
 */
public class ReusableCountLatch {

    private final Sync sync;

    public ReusableCountLatch(int initialCount) {
        if (initialCount < 0) {
            throw new IllegalArgumentException("negative initial count '" + initialCount + "' is not allowed");
        }
        this.sync = new Sync(initialCount);
    }

    public ReusableCountLatch() {
        this(0);
    }

    public int getCount() {
        return sync.getCount();
    }

    public void increment() {
        sync.increment();
    }

    public void decrement() {
        sync.decrement();
    }

    public void waitTillZero() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean waitTillZero(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    // class based on CountDownLatch.Sync
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        protected int getCount() {
            return getState();
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
